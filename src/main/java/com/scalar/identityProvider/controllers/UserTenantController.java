package com.scalar.identityProvider.controllers;

import com.scalar.identityProvider.models.User;
import com.scalar.identityProvider.models.UserTenantRole;
import com.scalar.identityProvider.payload.request.AddUserToTenantRequest;
import com.scalar.identityProvider.payload.response.MessageResponse;
import com.scalar.identityProvider.repository.UserRepository;
import com.scalar.identityProvider.services.TenantService;
import com.scalar.identityProvider.services.UserTenantRoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin/tenant-users")
public class UserTenantController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private UserTenantRoleService userTenantRoleService;

    /**
     * Agregar un usuario existente a un tenant con roles específicos.
     * Solo accesible para administradores.
     *
     * @param addUserRequest La petición para agregar usuario a tenant.
     * @return ResponseEntity con el resultado de la operación.
     */
    @PostMapping("/add-user")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> addUserToTenant(@Valid @RequestBody AddUserToTenantRequest addUserRequest) {
        
        // Verificar que el tenant existe
        if (!tenantService.existsByTenantId(addUserRequest.getTenantId())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Tenant no encontrado!"));
        }

        // Buscar el usuario por username
        List<User> users = userRepository.findByUsername(addUserRequest.getUsername());
        if (users.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Usuario no encontrado!"));
        }

        // Si hay múltiples usuarios con el mismo username, tomar el primero
        // En un sistema real, podrías querer ser más específico
        User user = users.get(0);

        // Verificar si el usuario ya tiene roles en este tenant
        if (userTenantRoleService.getUserRolesInTenant(user.getId(), addUserRequest.getTenantId()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: El usuario ya tiene roles asignados en este tenant!"));
        }

        // Asignar roles por defecto si no se especifican
        Set<String> roles = addUserRequest.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = Set.of("user");
        }

        // Asignar roles al usuario en el tenant
        userTenantRoleService.assignRolesToUser(user.getId(), addUserRequest.getTenantId(), roles);

        return ResponseEntity.ok(new MessageResponse("Usuario agregado al tenant exitosamente!"));
    }

    /**
     * Obtener todos los usuarios de un tenant específico.
     * Solo accesible para administradores.
     *
     * @param tenantId El ID del tenant.
     * @return ResponseEntity con la lista de usuarios del tenant.
     */
    @GetMapping("/tenant/{tenantId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getTenantUsers(@PathVariable String tenantId) {
        
        // Verificar que el tenant existe
        if (!tenantService.existsByTenantId(tenantId)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Tenant no encontrado!"));
        }

        List<UserTenantRole> tenantUsers = userTenantRoleService.getTenantUsers(tenantId);
        return ResponseEntity.ok(tenantUsers);
    }

    /**
     * Obtener todos los tenants de un usuario específico.
     * Solo accesible para administradores.
     *
     * @param userId El ID del usuario.
     * @return ResponseEntity con la lista de tenants del usuario.
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getUserTenants(@PathVariable String userId) {
        
        // Verificar que el usuario existe
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Usuario no encontrado!"));
        }

        List<UserTenantRole> userTenants = userTenantRoleService.getUserTenants(userId);
        return ResponseEntity.ok(userTenants);
    }

    /**
     * Actualizar roles de un usuario en un tenant específico.
     * Solo accesible para administradores.
     *
     * @param userId El ID del usuario.
     * @param tenantId El ID del tenant.
     * @param roles Los nuevos roles.
     * @return ResponseEntity con el resultado de la operación.
     */
    @PutMapping("/user/{userId}/tenant/{tenantId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> updateUserRolesInTenant(
            @PathVariable String userId,
            @PathVariable String tenantId,
            @RequestBody Set<String> roles) {
        
        // Verificar que el usuario existe
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Usuario no encontrado!"));
        }

        // Verificar que el tenant existe
        if (!tenantService.existsByTenantId(tenantId)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Tenant no encontrado!"));
        }

        // Asignar roles por defecto si no se especifican
        if (roles == null || roles.isEmpty()) {
            roles = Set.of("user");
        }

        // Actualizar roles del usuario en el tenant
        userTenantRoleService.assignRolesToUser(userId, tenantId, roles);

        return ResponseEntity.ok(new MessageResponse("Roles del usuario actualizados exitosamente!"));
    }

    /**
     * Remover un usuario de un tenant específico.
     * Solo accesible para administradores.
     *
     * @param userId El ID del usuario.
     * @param tenantId El ID del tenant.
     * @return ResponseEntity con el resultado de la operación.
     */
    @DeleteMapping("/user/{userId}/tenant/{tenantId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> removeUserFromTenant(
            @PathVariable String userId,
            @PathVariable String tenantId) {
        
        // Verificar que el usuario existe
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Usuario no encontrado!"));
        }

        // Verificar que el tenant existe
        if (!tenantService.existsByTenantId(tenantId)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Tenant no encontrado!"));
        }

        // Remover usuario del tenant
        userTenantRoleService.removeUserFromTenant(userId, tenantId);

        return ResponseEntity.ok(new MessageResponse("Usuario removido del tenant exitosamente!"));
    }
}
