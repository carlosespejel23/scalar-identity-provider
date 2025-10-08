package com.scalar.identityProvider.controllers;

import com.scalar.identityProvider.models.User;
import com.scalar.identityProvider.models.UserTenantRole;
import com.scalar.identityProvider.payload.request.CreateUserRequest;
import com.scalar.identityProvider.payload.request.UpdateUserRequest;
import com.scalar.identityProvider.payload.response.MessageResponse;
import com.scalar.identityProvider.repository.UserRepository;
import com.scalar.identityProvider.security.services.UserDetailsImpl;
import com.scalar.identityProvider.services.UserTenantRoleService;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;


/*
 * Controller for users management globally.
 * These endpoinds are exclusively for SUPER_ADMIN.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin/users")
public class UserManagementController {

    /*
     * Dependencies
     */
    private UserRepository userRepository;

    private UserTenantRoleService userTenantRoleService;

    private PasswordEncoder encoder;


    /*
     * Constructors
     */
    public UserManagementController() {}

    @Autowired
    public UserManagementController(
        UserRepository userRepository, 
        UserTenantRoleService userTenantRoleService, 
        PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.userTenantRoleService = userTenantRoleService;
        this.encoder = encoder;
    }

    
    /**
     * Create a new user.
     *
     * @param createUserRequest The user creation request.
     * @return ResponseEntity with the result of the operation.
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {

        // Verify if the email is already taken
        if (userRepository.existsByEmail(createUserRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("error", "Email is already taken"));
        }
        
        // Check if the username is already taken for this tenant
        if (userRepository.existsByUsernameAndTenantId(createUserRequest.getUsername(), createUserRequest.getTenantId())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("error", "Username is already taken"));
        }

        // Check if the email is already in use for this tenant
        if (userRepository.existsByEmailAndTenantId(createUserRequest.getEmail(), createUserRequest.getTenantId())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("error", "Email is already in use"));
        }

        // Get current timestamp
		String now = Instant.now().toString();

        // Create a new user's account
        User user = new User(
            createUserRequest.getUsername(),
            createUserRequest.getFirstName(),
            createUserRequest.getLastName(),
            createUserRequest.getEmail(),
            encoder.encode(createUserRequest.getPassword()),
            createUserRequest.getTenantId(),
            now, // createdAt
            now // updatedAt
        );

        Set<String> strRoles = createUserRequest.getRoles();
        
        userRepository.save(user);

        // Assign default roles or those provided using UserTenantRoleService
        if (strRoles == null || strRoles.isEmpty()) {
            userTenantRoleService.assignRolesToUser(user.getId(), createUserRequest.getTenantId(), Set.of("user"));
        } else {
            userTenantRoleService.assignRolesToUser(user.getId(), createUserRequest.getTenantId(), strRoles);
        }

        return ResponseEntity.ok(new MessageResponse("success", "User created successfully"));
    }


    /**
     * Get all users.
     *
     * @return ResponseEntity with the list of users.
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }


    /**
     * Get a user by their ID.
     *
     * @param userId User ID.
     * @return ResponseEntity with the user or error message.
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable String userId) {
        Optional<User> user = userRepository.findById(userId);
        
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("error", "User not found"));
        }
    }


    /**
     * Update an user.
     *
     * @param userId User ID.
     * @param updateUserRequest The update request.
     * @return ResponseEntity with the result of the operation.
     */
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable String userId, @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (!userOpt.isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("error", "User not found"));
        }

        User user = userOpt.get();

        // Update email if provided
        if (updateUserRequest.getEmail() != null && !updateUserRequest.getEmail().isEmpty()) {
            // Check if the new email is already in use by another user
            if (userRepository.existsByEmail(updateUserRequest.getEmail()) && 
                !updateUserRequest.getEmail().equals(user.getEmail())) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("error", "Email is already in use"));
            }
            user.setEmail(updateUserRequest.getEmail());
        }

        // Update password if provided
        if (updateUserRequest.getPassword() != null && !updateUserRequest.getPassword().isEmpty()) {
            user.setPassword(encoder.encode(updateUserRequest.getPassword()));
        }

        // Update roles if provided using UserTenantRoleService
        if (updateUserRequest.getRoles() != null && !updateUserRequest.getRoles().isEmpty()) {
            userTenantRoleService.assignRolesToUser(user.getId(), user.getTenantId(), updateUserRequest.getRoles());
        }

        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("success", "User updated successfully"));
    }


    /**
     * Deactivate user.
     * 
     * @param userId User ID.
     * @return ResponseEntity with the result of the operation.
     */
    @PutMapping("/{userId}/deactivate")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deactivateUser(@PathVariable String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (!userOpt.isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("error", "User not found"));
        }

        User user = userOpt.get();

        // Verify that you are not deactivating yourself
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        if (user.getId().equals(userDetails.getId())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("error", "You cannot deactivate yourself."));
        }

        user.setActive(false);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("success", "User deactivated successfully"));
    }


    /**
     * Reactivate user.
     * 
     * @param userId User ID.
     * @return ResponseEntity with the result of the operation.
     */
    @PutMapping("/{userId}/reactivate")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> reactivateUser(@PathVariable String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (!userOpt.isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("error", "User not found"));
        }

        User user = userOpt.get();
        user.setActive(true);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("success", "User reactivated successfully"));
    }


    /**
     * Delete an user.
     *
     * @param userId User ID.
     * @return ResponseEntity with the result of the operation.
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (!userOpt.isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("success", "User not found"));
        }

        // Verify that you are not deleting yourself
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        if (userOpt.get().getId().equals(userDetails.getId())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("error", "You cannot delete yourself."));
        }

        userRepository.deleteById(userId);
        return ResponseEntity.ok(new MessageResponse("success", "User deleted successfully"));
    }


    /**
     * Get all tenants for a specific user.
     *
     * @param userId User ID.
     * @return ResponseEntity with the list of the user's tenants.
     */
    @GetMapping("/tenants-user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getUserTenants(@PathVariable String userId) {
        
        // Verify that the user exists
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("error", "User not found"));
        }

        List<UserTenantRole> userTenants = userTenantRoleService.getUserTenants(userId);
        return ResponseEntity.ok(userTenants);
    }
}
