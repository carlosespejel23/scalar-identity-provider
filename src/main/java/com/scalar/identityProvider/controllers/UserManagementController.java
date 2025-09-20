package com.scalar.identityProvider.controllers;

import com.scalar.identityProvider.models.EmployeeRole;
import com.scalar.identityProvider.models.Role;
import com.scalar.identityProvider.models.User;
import com.scalar.identityProvider.payload.request.CreateUserRequest;
import com.scalar.identityProvider.payload.request.UpdateUserRequest;
import com.scalar.identityProvider.payload.response.MessageResponse;
import com.scalar.identityProvider.repository.RoleRepository;
import com.scalar.identityProvider.repository.UserRepository;
import com.scalar.identityProvider.security.TenantContext;
import com.scalar.identityProvider.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin/users")
public class UserManagementController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    /**
     * Crear un nuevo usuario en el tenant actual.
     * Solo accesible para administradores.
     *
     * @param createUserRequest La petición de creación de usuario.
     * @return ResponseEntity con el resultado de la operación.
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        
        String tenantId = TenantContext.getCurrentTenant();
        
        // Check if the username is already taken for this tenant
        if (userRepository.existsByUsernameAndTenantId(createUserRequest.getUsername(), tenantId)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        // Check if the email is already in use for this tenant
        if (userRepository.existsByEmailAndTenantId(createUserRequest.getEmail(), tenantId)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create a new user's account
        User user = new User(createUserRequest.getUsername(),
                createUserRequest.getEmail(),
                encoder.encode(createUserRequest.getPassword()),
                tenantId);

        Set<String> strRoles = createUserRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        // Assign roles based on the request or default to user role
        if (strRoles == null || strRoles.isEmpty()) {
            Role userRole = roleRepository.findByNameAndTenantId(EmployeeRole.ROLE_USER, tenantId)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByNameAndTenantId(EmployeeRole.ROLE_ADMIN, tenantId)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "mod":
                        Role modRole = roleRepository.findByNameAndTenantId(EmployeeRole.ROLE_MODERATOR, tenantId)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByNameAndTenantId(EmployeeRole.ROLE_USER, tenantId)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        // Assign roles to the user and save it to the database
        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User created successfully!"));
    }

    /**
     * Obtener todos los usuarios del tenant actual.
     * Solo accesible para administradores.
     *
     * @return ResponseEntity con la lista de usuarios.
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        String tenantId = TenantContext.getCurrentTenant();
        List<User> users = userRepository.findByTenantId(tenantId);
        return ResponseEntity.ok(users);
    }

    /**
     * Obtener un usuario por su ID.
     * Solo accesible para administradores.
     *
     * @param userId El ID del usuario.
     * @return ResponseEntity con el usuario o mensaje de error.
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable String userId) {
        String tenantId = TenantContext.getCurrentTenant();
        Optional<User> user = userRepository.findById(userId);
        
        if (user.isPresent() && user.get().getTenantId().equals(tenantId)) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: User not found!"));
        }
    }

    /**
     * Actualizar un usuario.
     * Solo accesible para administradores.
     *
     * @param userId El ID del usuario.
     * @param updateUserRequest La petición de actualización.
     * @return ResponseEntity con el resultado de la operación.
     */
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable String userId, @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        String tenantId = TenantContext.getCurrentTenant();
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (!userOpt.isPresent() || !userOpt.get().getTenantId().equals(tenantId)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: User not found!"));
        }

        User user = userOpt.get();

        // Update email if provided
        if (updateUserRequest.getEmail() != null && !updateUserRequest.getEmail().isEmpty()) {
            // Check if the new email is already in use by another user
            if (userRepository.existsByEmailAndTenantId(updateUserRequest.getEmail(), tenantId) && 
                !updateUserRequest.getEmail().equals(user.getEmail())) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Email is already in use!"));
            }
            user.setEmail(updateUserRequest.getEmail());
        }

        // Update password if provided
        if (updateUserRequest.getPassword() != null && !updateUserRequest.getPassword().isEmpty()) {
            user.setPassword(encoder.encode(updateUserRequest.getPassword()));
        }

        // Update roles if provided
        if (updateUserRequest.getRoles() != null && !updateUserRequest.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            updateUserRequest.getRoles().forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByNameAndTenantId(EmployeeRole.ROLE_ADMIN, tenantId)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "mod":
                        Role modRole = roleRepository.findByNameAndTenantId(EmployeeRole.ROLE_MODERATOR, tenantId)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByNameAndTenantId(EmployeeRole.ROLE_USER, tenantId)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
            user.setRoles(roles);
        }

        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User updated successfully!"));
    }

    /**
     * Eliminar un usuario.
     * Solo accesible para administradores.
     *
     * @param userId El ID del usuario.
     * @return ResponseEntity con el resultado de la operación.
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        String tenantId = TenantContext.getCurrentTenant();
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (!userOpt.isPresent() || !userOpt.get().getTenantId().equals(tenantId)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: User not found!"));
        }

        // Verificar que no se está eliminando a sí mismo
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        if (userOpt.get().getId().equals(userDetails.getId())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: No puedes eliminarte a ti mismo!"));
        }

        userRepository.deleteById(userId);
        return ResponseEntity.ok(new MessageResponse("User deleted successfully!"));
    }
}
