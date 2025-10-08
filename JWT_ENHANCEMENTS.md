# Mejoras del Sistema JWT - Logout y Refresh Tokens

## Resumen de Implementación

Se han implementado las siguientes mejoras al sistema de autenticación JWT:

1. **Sistema de Blacklist para Tokens Revocados**
2. **Refresh Tokens con Expiración Mensual**
3. **Endpoint de Logout**
4. **Endpoint de Refresh Token**
5. **Limpieza Automática de Tokens Expirados**

## Nuevos Endpoints

### 1. POST /api/auth/signout
**Descripción:** Cierra la sesión del usuario y revoca todos sus tokens.

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Respuesta Exitosa:**
```json
{
  "status": "success",
  "message": "Sesión cerrada exitosamente"
}
```

**Respuesta de Error:**
```json
{
  "status": "error",
  "message": "Usuario no autenticado"
}
```

### 2. POST /api/auth/refresh/{tenantId}
**Descripción:** Renueva el JWT token para un tenant específico. Este endpoint es más seguro para sistemas multitenant ya que valida que el refresh token pertenezca al tenant especificado.

**Path Parameters:**
- `tenantId`: ID del tenant para el cual renovar el token

**Request Body:**
```json
{
  "refreshToken": "uuid-refresh-token"
}
```

**Respuesta Exitosa:**
```json
{
  "status": "success",
  "accessToken": "new-jwt-token",
  "refreshToken": "new-refresh-token",
  "tokenType": "Bearer",
  "message": "Tokens renovados exitosamente para el tenant: tenant-id"
}
```

**Respuesta de Error:**
```json
{
  "status": "error",
  "message": "Refresh token inválido, expirado o no pertenece al tenant: tenant-id"
}
```

**Ventajas de Seguridad:**
- Validación explícita de tenant
- Previene ataques de escalación de privilegios
- Requiere que el refresh token pertenezca al tenant especificado

## Cambios en Endpoints Existentes

### POST /api/auth/signin
**Cambio:** Ahora retorna tanto JWT como refresh token.

**Nueva Respuesta:**
```json
{
  "status": "success",
  "accessToken": "jwt-token",
  "refreshToken": "refresh-token",
  "tokenType": "Bearer",
  "message": "User signed in successfully!"
}
```

### POST /api/auth/signup
**Cambio:** Ahora retorna tanto JWT como refresh token.

**Nueva Respuesta:**
```json
{
  "status": "success",
  "accessToken": "jwt-token",
  "refreshToken": "refresh-token",
  "tokenType": "Bearer",
  "message": "User registered successfully!"
}
```

### POST /api/auth/switch-tenant
**Cambio:** Ahora retorna tanto JWT como refresh token para el nuevo tenant.

**Request Body:**
```json
{
  "tenantId": "new-tenant-id"
}
```

**Nueva Respuesta:**
```json
{
  "status": "success",
  "accessToken": "new-jwt-token",
  "refreshToken": "new-refresh-token",
  "tokenType": "Bearer",
  "message": "Tenant switched successfully"
}
```

**Beneficios:**
- El cambio de tenant ahora persiste sin necesidad de login
- El usuario puede renovar tokens del nuevo tenant usando `/refresh/{tenantId}`
- Simplifica la lógica del frontend

## Configuración

### Variables de Entorno
Agregar las siguientes variables de entorno:

```bash
# Refresh Token Expiration (opcional, por defecto 30 días)
REFRESH_TOKEN_EXPIRATION=2592000000
```

### Propiedades de Aplicación
Las siguientes propiedades se han agregado a `application.properties`:

```properties
# Refresh Token configuration
refreshTokenExpirationMs= ${REFRESH_TOKEN_EXPIRATION:2592000000}
```

## Nuevos Modelos de Datos

### RevokedToken
Almacena tokens JWT revocados en la blacklist.

### RefreshToken
Almacena refresh tokens con información de usuario y tenant.

## Nuevos Servicios

### RefreshTokenService
- Genera refresh tokens
- Valida refresh tokens
- Revoca refresh tokens
- Limpia tokens expirados

### TokenBlacklistService
- Agrega tokens a la blacklist
- Verifica si un token está en la blacklist
- Limpia tokens expirados de la blacklist

### TokenCleanupService
- Limpieza automática cada hora
- Limpia tanto refresh tokens como tokens revocados expirados

## Flujo de Autenticación Mejorado

1. **Login/Signup:** Usuario recibe JWT (1 día) + Refresh Token (30 días)
2. **Uso Normal:** Cliente usa JWT para autenticación
3. **Token Expirado:** Cliente usa refresh token para obtener nuevo JWT
4. **Logout:** Todos los tokens del usuario son revocados
5. **Limpieza:** Tokens expirados se eliminan automáticamente

## Consideraciones de Seguridad

1. **Blacklist:** Los tokens revocados no pueden ser reutilizados
2. **Refresh Token Rotation:** Cada refresh genera un nuevo refresh token
3. **Limpieza Automática:** Los tokens expirados se eliminan automáticamente
4. **Validación:** Todos los tokens se validan contra la blacklist
5. **Seguridad Multitenant:** Los refresh tokens están vinculados a tenants específicos

## Seguridad Multitenant

### Validación de Tenant en Refresh Tokens
- Los refresh tokens están vinculados a un tenant específico
- No se puede usar un refresh token de un tenant para renovar tokens de otro tenant
- El endpoint `/refresh/{tenantId}` proporciona validación explícita de tenant
- Previene ataques de escalación de privilegios entre tenants

### Flujo Seguro para Sistemas Multitenant
1. **Login:** Usuario se autentica en un tenant específico
2. **Refresh Token:** Se genera vinculado al tenant del login
3. **Renovación:** Solo se puede renovar para el mismo tenant
4. **Cambio de Tenant:** Usa `/switch-tenant` que genera nuevos tokens
5. **Logout:** Revoca todos los tokens del tenant actual

### Flujo Completo para Frontend
```
1. Login inicial → JWT + Refresh Token (Tenant A)
2. Cambio de tenant → /switch-tenant → JWT + Refresh Token (Tenant B)
3. JWT expira → /refresh/{tenantB} → Nuevo JWT + Refresh Token (Tenant B)
4. Cambio a Tenant C → /switch-tenant → JWT + Refresh Token (Tenant C)
5. JWT expira → /refresh/{tenantC} → Nuevo JWT + Refresh Token (Tenant C)
```

**Ventajas para el Frontend:**
- Solo necesita almacenar JWT y Refresh Token actuales
- No necesita lógica compleja de manejo de múltiples tokens
- El cambio de tenant persiste automáticamente
- Renovación automática sin necesidad de login

### Mejores Prácticas
- **Usar solo `/refresh/{tenantId}`** para renovaciones de tokens (más seguro)
- Validar siempre que el refresh token pertenezca al tenant correcto
- Implementar rotación de refresh tokens en cada renovación
- Limpiar tokens expirados regularmente
- **Eliminar `/refresh` genérico** para evitar ambigüedades de seguridad

## Base de Datos

Se crean dos nuevas colecciones en MongoDB:
- `revoked_tokens`: Tokens JWT revocados
- `refresh_tokens`: Refresh tokens activos

## Notas de Implementación

- Los refresh tokens tienen una duración de 30 días por defecto
- La limpieza automática se ejecuta cada hora
- El sistema mantiene compatibilidad con la implementación anterior
- Los tokens revocados se validan en cada request
