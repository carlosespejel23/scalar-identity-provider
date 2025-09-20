# Guía de Multitenancy - Aplicación de Autenticación JWT

## Descripción

Esta aplicación Spring Boot implementa multitenancy con base de datos compartida usando MongoDB. Cada tenant tiene sus propios usuarios y roles, pero comparten la misma base de datos.

## Características Implementadas

### 1. Modelos de Datos
- **Tenant**: Entidad principal que representa una organización
- **User**: Usuarios con campo `tenantId` para aislamiento
- **Role**: Roles con campo `tenantId` para aislamiento por tenant

### 2. Contexto de Tenant
- **TenantContext**: ThreadLocal para mantener el tenant actual
- Se establece durante la autenticación y se limpia al final de cada request

### 3. Seguridad JWT
- Los tokens JWT incluyen el `tenantId` como claim
- El filtro de autenticación extrae el tenant del token y establece el contexto

### 4. Repositorios
- Métodos específicos para filtrar por `tenantId`
- Aislamiento automático de datos por tenant

### 5. Flujo Optimizado
- **Registro automático de tenant**: Se crea automáticamente durante el signup
- **Generación automática de tenantId**: Conversión de nombre a ID válido
- **Login optimizado**: Requiere tenantId para mejor rendimiento
- **Signup restringido**: Solo para crear administradores de tenant
- **Gestión de usuarios**: Los admins pueden crear/editar/eliminar usuarios
- **Roles globales**: Sistema de roles reutilizables con SUPER_ADMIN
- **Usuarios multi-tenant**: Agregar usuarios existentes a múltiples tenants
- **Cambio de tenant**: Funcionalidad para cambiar tenant dentro de la sesión

## Endpoints Disponibles

### Gestión de Tenants

#### Crear Tenant
```http
POST /api/tenants/create
Content-Type: application/json

{
    "name": "Empresa ABC",
    "tenantId": "empresa-abc",
    "description": "Empresa de ejemplo"
}
```

#### Listar Tenants
```http
GET /api/tenants/list
```

#### Obtener Tenant por ID
```http
GET /api/tenants/{id}
```

#### Obtener Tenant por TenantId
```http
GET /api/tenants/by-tenant-id/{tenantId}
```

### Autenticación

#### Registro de Administrador (con creación automática de tenant)
```http
POST /api/auth/signup
Content-Type: application/json

{
    "username": "admin",
    "email": "admin@empresa-abc.com",
    "password": "password123",
    "tenantName": "Empresa ABC"
}
```

#### Inicio de Sesión (optimizado con tenantId)
```http
POST /api/auth/signin
Content-Type: application/json

{
    "username": "admin",
    "password": "password123",
    "tenantId": "empresa-abc"
}
```

#### Cambio de Tenant (dentro de la sesión)
```http
POST /api/auth/switch-tenant
Content-Type: application/json
Authorization: Bearer <jwt-token>

{
    "tenantId": "otro-tenant"
}
```

#### Obtener Tenants del Usuario
```http
GET /api/auth/user-tenants
Authorization: Bearer <jwt-token>
```

### Gestión de Usuarios (Solo Administradores)

#### Crear Usuario
```http
POST /api/admin/users/create
Content-Type: application/json
Authorization: Bearer <jwt-token>

{
    "username": "usuario1",
    "email": "usuario1@empresa-abc.com",
    "password": "password123",
    "roles": ["user"]
}
```

#### Listar Usuarios del Tenant
```http
GET /api/admin/users/list
Authorization: Bearer <jwt-token>
```

#### Obtener Usuario por ID
```http
GET /api/admin/users/{userId}
Authorization: Bearer <jwt-token>
```

#### Actualizar Usuario
```http
PUT /api/admin/users/{userId}
Content-Type: application/json
Authorization: Bearer <jwt-token>

{
    "email": "nuevo-email@empresa-abc.com",
    "password": "nueva-password",
    "roles": ["mod"]
}
```

#### Eliminar Usuario
```http
DELETE /api/admin/users/{userId}
Authorization: Bearer <jwt-token>
```

### Gestión de Usuarios Multi-Tenant (Solo Administradores)

#### Agregar Usuario Existente a Tenant
```http
POST /api/admin/tenant-users/add-user
Content-Type: application/json
Authorization: Bearer <jwt-token>

{
    "username": "usuario_existente",
    "tenantId": "empresa-abc",
    "roles": ["user"]
}
```

#### Obtener Usuarios de un Tenant
```http
GET /api/admin/tenant-users/tenant/{tenantId}
Authorization: Bearer <jwt-token>
```

#### Obtener Tenants de un Usuario
```http
GET /api/admin/tenant-users/user/{userId}
Authorization: Bearer <jwt-token>
```

#### Actualizar Roles de Usuario en Tenant
```http
PUT /api/admin/tenant-users/user/{userId}/tenant/{tenantId}
Content-Type: application/json
Authorization: Bearer <jwt-token>

["user", "mod"]
```

#### Remover Usuario de Tenant
```http
DELETE /api/admin/tenant-users/user/{userId}/tenant/{tenantId}
Authorization: Bearer <jwt-token>
```

## Flujo de Trabajo

### 1. Registro de Administrador (Flujo Simplificado)
1. Usar `/api/auth/signup` con `tenantName` (nombre legible del tenant)
2. El sistema automáticamente:
   - Genera un `tenantId` válido a partir del `tenantName`
   - Crea el tenant (solo si no existe)
   - Inicializa los roles básicos (USER, MODERATOR, ADMIN)
   - Registra al usuario como ADMIN del tenant
3. **Importante**: Solo se puede crear un tenant por signup

### 2. Autenticación (Optimizada)
1. Usar `/api/auth/signin` con `username`, `password` y `tenantId`
2. El sistema:
   - Verifica que el tenant existe
   - Autentica las credenciales en el tenant específico
   - Retorna un JWT que incluye el `tenantId`
3. **Ventaja**: Rendimiento optimizado al no buscar en todos los tenants

### 3. Gestión de Usuarios (Solo Administradores)
1. El admin puede crear usuarios adicionales usando `/api/admin/users/create`
2. El admin puede listar, editar y eliminar usuarios del tenant
3. Los usuarios se crean solo dentro del tenant del admin
4. Se pueden asignar diferentes roles (USER, MODERATOR, ADMIN, SUPER_ADMIN)

### 4. Gestión Multi-Tenant (Solo Administradores)
1. Los admins pueden agregar usuarios existentes a otros tenants
2. Un usuario puede tener diferentes roles en diferentes tenants
3. Los roles son globales y reutilizables (no duplicados por tenant)
4. Se puede remover usuarios de tenants específicos

### 5. Cambio de Tenant (Dentro de la Sesión)
1. Usar `/api/auth/user-tenants` para ver todos los tenants disponibles
2. Usar `/api/auth/switch-tenant` con el `tenantId` deseado
3. El sistema genera un nuevo JWT con el nuevo tenant
4. Todas las operaciones posteriores se realizan en el nuevo tenant

### 6. Uso de APIs Protegidas
1. Incluir el JWT en el header `Authorization: Bearer <token>`
2. El sistema automáticamente establece el contexto del tenant
3. Todas las operaciones se realizan dentro del contexto del tenant

## Ejemplos de Uso

### Ejemplo Completo: Flujo Optimizado

```bash
# 1. Registrar administrador (crea tenant automáticamente)
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "email": "admin@mi-empresa.com",
    "password": "admin123",
    "tenantName": "Mi Empresa"
  }'

# 2. Iniciar sesión como admin (optimizado con tenantId)
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123",
    "tenantId": "mi-empresa"
  }'

# 3. Crear usuario normal (solo admin puede hacerlo)
curl -X POST http://localhost:8080/api/admin/users/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <jwt-token>" \
  -d '{
    "username": "usuario1",
    "email": "usuario1@mi-empresa.com",
    "password": "user123",
    "roles": ["user"]
  }'

# 4. Listar usuarios del tenant
curl -X GET http://localhost:8080/api/admin/users/list \
  -H "Authorization: Bearer <jwt-token>"

# 5. Ver tenants disponibles para el usuario
curl -X GET http://localhost:8080/api/auth/user-tenants \
  -H "Authorization: Bearer <jwt-token>"

# 6. Cambiar a otro tenant (si el usuario tiene acceso)
curl -X POST http://localhost:8080/api/auth/switch-tenant \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <jwt-token>" \
  -d '{
    "tenantId": "otro-tenant"
  }'
```

### Ejemplo: Gestión Completa de Usuarios

```bash
# 1. Registrar administrador del primer tenant
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin1",
    "email": "admin1@empresa1.com",
    "password": "admin123",
    "tenantName": "Empresa 1"
  }'

# 2. Login como admin del primer tenant
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin1",
    "password": "admin123",
    "tenantId": "empresa-1"
  }'

# 3. Crear usuarios en el primer tenant
curl -X POST http://localhost:8080/api/admin/users/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <jwt-token>" \
  -d '{
    "username": "usuario1",
    "email": "usuario1@empresa1.com",
    "password": "user123",
    "roles": ["user"]
  }'

curl -X POST http://localhost:8080/api/admin/users/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <jwt-token>" \
  -d '{
    "username": "moderador1",
    "email": "mod1@empresa1.com",
    "password": "mod123",
    "roles": ["mod"]
  }'

# 4. Listar todos los usuarios del tenant
curl -X GET http://localhost:8080/api/admin/users/list \
  -H "Authorization: Bearer <jwt-token>"

# 5. Actualizar un usuario
curl -X PUT http://localhost:8080/api/admin/users/{userId} \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <jwt-token>" \
  -d '{
    "email": "nuevo-email@empresa1.com",
    "roles": ["mod"]
  }'

# 6. Eliminar un usuario
curl -X DELETE http://localhost:8080/api/admin/users/{userId} \
  -H "Authorization: Bearer <jwt-token>"

# 7. Agregar usuario existente a otro tenant
curl -X POST http://localhost:8080/api/admin/tenant-users/add-user \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <jwt-token>" \
  -d '{
    "username": "usuario1",
    "tenantId": "otro-tenant",
    "roles": ["mod"]
  }'

# 8. Ver usuarios de un tenant específico
curl -X GET http://localhost:8080/api/admin/tenant-users/tenant/empresa-1 \
  -H "Authorization: Bearer <jwt-token>"

# 9. Ver tenants de un usuario específico
curl -X GET http://localhost:8080/api/admin/tenant-users/user/{userId} \
  -H "Authorization: Bearer <jwt-token>"
```

## Consideraciones de Seguridad

1. **Aislamiento de Datos**: Cada tenant solo puede acceder a sus propios datos
2. **Validación de Tenant**: Se verifica que el tenant existe antes de cualquier operación
3. **Contexto Thread-Safe**: El contexto del tenant se maneja de forma thread-safe
4. **Limpieza de Contexto**: El contexto se limpia automáticamente al final de cada request
5. **Control de Acceso**: Solo administradores pueden gestionar usuarios
6. **Signup Restringido**: Solo se puede crear un tenant por signup
7. **Rendimiento Optimizado**: Login directo por tenantId sin búsquedas múltiples
8. **Roles Globales**: Sistema de roles reutilizables sin duplicación
9. **SUPER_ADMIN**: Rol con permisos globales del sistema

## Estructura de Base de Datos

### Colección: tenants
```json
{
  "_id": "ObjectId",
  "name": "Nombre del Tenant",
  "tenantId": "identificador-unico",
  "description": "Descripción opcional",
  "active": true
}
```

### Colección: users
```json
{
  "_id": "ObjectId",
  "username": "nombre_usuario",
  "email": "email@tenant.com",
  "password": "hash_password",
  "tenantId": "identificador-tenant",
  "roles": ["ObjectId_refs_to_roles"]
}
```

### Colección: global_roles
```json
{
  "_id": "ObjectId",
  "name": "ROLE_USER|ROLE_MODERATOR|ROLE_ADMIN|ROLE_SUPER_ADMIN",
  "description": "Descripción del rol",
  "active": true
}
```

### Colección: user_tenant_roles
```json
{
  "_id": "ObjectId",
  "userId": "ObjectId_del_usuario",
  "tenantId": "identificador-tenant",
  "roles": ["ObjectId_refs_to_global_roles"]
}
```

## Notas de Implementación

- Los roles se inicializan automáticamente cuando se crea un nuevo tenant
- El sistema soporta múltiples tenants simultáneamente
- Cada tenant puede tener usuarios con el mismo username (aislamiento por tenant)
- Los emails también son únicos por tenant
- El JWT incluye el tenantId para mantener el contexto en requests posteriores
- **Generación automática de tenantId**: Los nombres se convierten a minúsculas y espacios a guiones
- **Login optimizado**: Requiere tenantId para mejor rendimiento (no busca en todos los tenants)
- **Signup restringido**: Solo crea administradores y un tenant por signup
- **Gestión de usuarios**: Solo administradores pueden crear/editar/eliminar usuarios
- **Roles globales**: Sistema de roles reutilizables sin duplicación por tenant
- **SUPER_ADMIN**: Rol con permisos globales del sistema
- **Usuarios multi-tenant**: Agregar usuarios existentes a múltiples tenants
- **Cambio de tenant en sesión**: Los usuarios pueden cambiar entre tenants sin re-autenticarse
- **Control de acceso**: Endpoints de administración protegidos con roles

