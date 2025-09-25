package com.scalar.identityProvider.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.scalar.identityProvider.models.EmployeeRole;
import com.scalar.identityProvider.models.Role;
import com.scalar.identityProvider.models.Tenant;
import com.scalar.identityProvider.models.User;
import com.scalar.identityProvider.payload.request.LoginRequest;
import com.scalar.identityProvider.payload.request.SignupRequest;
import com.scalar.identityProvider.payload.request.SwitchTenantRequest;
import com.scalar.identityProvider.payload.response.JwtResponse;
import com.scalar.identityProvider.payload.response.MessageResponse;
import com.scalar.identityProvider.repository.RoleRepository;
import com.scalar.identityProvider.repository.TenantRepository;
import com.scalar.identityProvider.repository.UserRepository;
import com.scalar.identityProvider.security.TenantContext;
import com.scalar.identityProvider.services.RoleInitializationService;
import com.scalar.identityProvider.services.TenantService;
import com.scalar.identityProvider.services.UserTenantService;
import com.scalar.identityProvider.utils.TenantUtils;
import com.scalar.identityProvider.security.jwt.JwtUtils;
import com.scalar.identityProvider.security.services.UserDetailsImpl;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600) // Allow cross-origin requests for all origins
@RestController // Indicate that this class is a REST controller
@RequestMapping("/api/auth") // Base URL for authentication-related endpoints
public class AuthController {

	@Autowired
	AuthenticationManager authenticationManager; // Handles user authentication

	@Autowired
	UserRepository userRepository; // Repository for user-related database operations

	@Autowired
	RoleRepository roleRepository; // Repository for role-related database operations

	@Autowired
	PasswordEncoder encoder; // Encoder for password hashing

	@Autowired
	JwtUtils jwtUtils; // Utility for generating JWT tokens

	@Autowired
	TenantService tenantService; // Service for tenant operations

	@Autowired
	TenantRepository tenantRepository; // Repository for tenant operations

	@Autowired
	UserTenantService userTenantService; // Service for user-tenant operations

	@Autowired
	RoleInitializationService roleInitializationService; // Service for role initialization

	/**
	 * Authenticate user and return a JWT token if successful.
	 *
	 * @param loginRequest The login request containing username and password.
	 * @return A ResponseEntity containing the JWT response or an error message.
	 */
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		// Verificar que el tenant existe
		if (!tenantService.existsByTenantId(loginRequest.getTenantId())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Tenant no encontrado!"));
		}

		// Establecer el contexto del tenant
		TenantContext.setCurrentTenant(loginRequest.getTenantId());

		try {
			// Authenticate the user with the provided username and password
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
							loginRequest.getPassword()));

			// Set the authentication in the security context
			SecurityContextHolder.getContext().setAuthentication(authentication);

			// Generate JWT token based on the authentication
			String jwt = jwtUtils.generateJwtToken(authentication);

			// Get user details from the authentication object
			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

			// Extract user roles into a list
			List<String> roles = userDetails.getAuthorities().stream()
					.map(item -> item.getAuthority())
					.collect(Collectors.toList());

			// Return a response containing the JWT and user details
			return ResponseEntity.ok(new JwtResponse(jwt,
					userDetails.getId(),
					userDetails.getUsername(),
					userDetails.getEmail(),
					roles));
		} finally {
			// Limpiar el contexto del tenant
			TenantContext.clear();
		}
	}

	/**
	 * Register a new user account.
	 *
	 * @param signUpRequest The signup request containing user details.
	 * @return A ResponseEntity indicating success or error message.
	 */
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

		// Generar tenantId a partir del nombre del tenant
		String tenantId = TenantUtils.generateTenantId(signUpRequest.getTenantName());

		// Verificar si el tenant ya existe
		if (tenantService.existsByTenantId(tenantId)) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: El tenant ya existe! Solo se puede crear un tenant por signup."));
		}

		// Crear el nuevo tenant
		Tenant tenant = new Tenant(signUpRequest.getTenantName(), tenantId);
		tenant = tenantService.createTenant(tenant);

		// Inicializar roles para el nuevo tenant
		roleInitializationService.initializeRolesForTenant(tenantId);

		// Establecer el contexto del tenant
		TenantContext.setCurrentTenant(tenantId);

		try {
			// Check if the username is already taken for this tenant
			if (userRepository.existsByUsernameAndTenantId(signUpRequest.getUsername(), tenantId)) {
				return ResponseEntity
						.badRequest()
						.body(new MessageResponse("Error: Username is already taken!"));
			}

			// Check if the email is already in use for this tenant
			if (userRepository.existsByEmailAndTenantId(signUpRequest.getEmail(), tenantId)) {
				return ResponseEntity
						.badRequest()
						.body(new MessageResponse("Error: Email is already in use!"));
			}

			// Create a new user's account - Solo admin en signup
			User user = new User(
				signUpRequest.getUsername(),
				signUpRequest.getFirstName(),
				signUpRequest.getLastName(),
				signUpRequest.getEmail(),
				encoder.encode(signUpRequest.getPassword()),
				tenantId
			);

			// En signup, el usuario siempre es admin del tenant
			Set<Role> roles = new HashSet<>();
			Role adminRole = roleRepository.findByNameAndTenantId(EmployeeRole.ROLE_ADMIN, tenantId)
					.orElseThrow(() -> new RuntimeException("Error: Admin role not found."));
			roles.add(adminRole);

			// Assign roles to the user and save it to the database
			user.setRoles(roles);
			userRepository.save(user);

			// Return a success message upon successful registration
			return ResponseEntity.ok(new MessageResponse("Admin user registered successfully!"));
		} finally {
			// Limpiar el contexto del tenant
			TenantContext.clear();
		}
	}

	/**
	 * Cambiar el tenant actual del usuario autenticado.
	 *
	 * @param switchRequest La petición de cambio de tenant.
	 * @return ResponseEntity con el nuevo JWT o mensaje de error.
	 */
	@PostMapping("/switch-tenant")
	public ResponseEntity<?> switchTenant(@Valid @RequestBody SwitchTenantRequest switchRequest) {
		
		// Obtener el usuario actual del contexto de seguridad
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Usuario no autenticado!"));
		}

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		String username = userDetails.getUsername();

		// Verificar que el usuario existe en el nuevo tenant
		if (!userTenantService.userExistsInTenant(username, switchRequest.getTenantId())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Usuario no existe en ese tenant!"));
		}

		// Establecer el contexto del nuevo tenant
		TenantContext.setCurrentTenant(switchRequest.getTenantId());

		try {
			// Autenticar al usuario en el nuevo tenant
			Authentication newAuthentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(username, userDetails.getPassword()));

			// Set the authentication in the security context
			SecurityContextHolder.getContext().setAuthentication(newAuthentication);

			// Generate new JWT token with the new tenant
			String jwt = jwtUtils.generateJwtToken(newAuthentication);

			// Get user details from the new authentication object
			UserDetailsImpl newUserDetails = (UserDetailsImpl) newAuthentication.getPrincipal();

			// Extract user roles into a list
			List<String> roles = newUserDetails.getAuthorities().stream()
					.map(item -> item.getAuthority())
					.collect(Collectors.toList());

			// Return a response containing the new JWT and user details
			return ResponseEntity.ok(new JwtResponse(jwt,
					newUserDetails.getId(),
					newUserDetails.getUsername(),
					newUserDetails.getEmail(),
					roles));
		} finally {
			// Limpiar el contexto del tenant
			TenantContext.clear();
		}
	}

	/**
	 * Obtener todos los tenants disponibles para el usuario autenticado.
	 *
	 * @return ResponseEntity con la lista de tenants.
	 */
	@GetMapping("/user-tenants")
	public ResponseEntity<?> getUserTenants() {
		
		// Obtener el usuario actual del contexto de seguridad
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Usuario no autenticado!"));
		}

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		String username = userDetails.getUsername();

		// Obtener todos los tenants donde el usuario tiene cuenta
		List<Tenant> userTenants = userTenantService.getTenantsForUser(username);

		return ResponseEntity.ok(userTenants);
	}
}
