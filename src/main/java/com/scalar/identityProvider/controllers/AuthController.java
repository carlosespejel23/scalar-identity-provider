package com.scalar.identityProvider.controllers;

// Models
import com.scalar.identityProvider.models.Tenant;
import com.scalar.identityProvider.models.User;
import com.scalar.identityProvider.models.RefreshToken;
// Payloads
import com.scalar.identityProvider.payload.request.LoginRequest;
import com.scalar.identityProvider.payload.request.RefreshTokenRequest;
import com.scalar.identityProvider.payload.request.SignupRequest;
import com.scalar.identityProvider.payload.request.SwitchTenantRequest;
import com.scalar.identityProvider.payload.response.MessageResponse;
import com.scalar.identityProvider.payload.response.RefreshTokenResponse;
import com.scalar.identityProvider.payload.response.UserDetailsResponse;
// Repositories
import com.scalar.identityProvider.repository.UserRepository;
// Security and Services
import com.scalar.identityProvider.security.TenantContext;
import com.scalar.identityProvider.security.jwt.JwtUtils;
import com.scalar.identityProvider.security.services.UserDetailsImpl;
import com.scalar.identityProvider.services.RefreshTokenService;
import com.scalar.identityProvider.services.PermissionService;
import com.scalar.identityProvider.services.UserTenantRoleService;
import com.scalar.identityProvider.services.TenantService;
import com.scalar.identityProvider.services.TokenBlacklistService;
import com.scalar.identityProvider.services.UserTenantService;
import com.scalar.identityProvider.utils.TenantUtils;


// Other imports
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * Controller for handling authentication-related endpoints such as signin, signup, etc.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	/*
	 * Dependencies
	 */
	private AuthenticationManager authenticationManager;

	private UserRepository userRepository;

	private PasswordEncoder encoder;

	private JwtUtils jwtUtils;

	private TenantService tenantService;

	private UserTenantService userTenantService;

	private RefreshTokenService refreshTokenService;

	private TokenBlacklistService tokenBlacklistService;

	private UserTenantRoleService userTenantRoleService;

	private PermissionService permissionService;


	/**
	 * Constructors
	 * 
	 * @param authenticationManager  The authentication manager.
	 * @param userRepository         The user repository.
	 * @param encoder                The password encoder.
	 * @param jwtUtils               The JWT utility class.
	 * @param tenantService          The tenant service.
	 * @param userTenantService      The user-tenant service.
	 * @param refreshTokenService    The refresh token service.
	 * @param tokenBlacklistService  The token blacklist service.
	 * @param userTenantRoleService  The user-tenant-role service.
	 * @param permissionService      The permission service.
	 */
	public AuthController(
		AuthenticationManager authenticationManager,
		UserRepository userRepository,
		PasswordEncoder encoder,
		JwtUtils jwtUtils,
		TenantService tenantService,
		UserTenantService userTenantService,
		RefreshTokenService refreshTokenService,
		TokenBlacklistService tokenBlacklistService,
		UserTenantRoleService userTenantRoleService,
		PermissionService permissionService) {
		this.authenticationManager = authenticationManager;
		this.userRepository = userRepository;
		this.encoder = encoder;
		this.jwtUtils = jwtUtils;
		this.tenantService = tenantService;
		this.userTenantService = userTenantService;
		this.refreshTokenService = refreshTokenService;
		this.tokenBlacklistService = tokenBlacklistService;
		this.userTenantRoleService = userTenantRoleService;
		this.permissionService = permissionService;
	}


	/**
	 * Authenticate user and return a JWT, refresh token if successful.
	 *
	 * @param loginRequest The login request containing username, password and tenant ID.
	 * @return A ResponseEntity containing the JWT, refresh token response or an error message.
	 */
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		// Verify if the tenant exists
		if (!tenantService.existsByTenantId(loginRequest.getTenantId())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("error", "Tenant not found!"));
		}

		// Verify if the user exists in the specified tenant
		if (!userTenantService.userExistsInTenant(loginRequest.getUsername(), loginRequest.getTenantId())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("error", "User not found in the specified tenant!"));
		}

		// Verify if the user is active
		if (!userRepository.isUserActive(loginRequest.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("error", "User is not active!"));
		}

		// Verify if the user is active in the specified tenant
		if (!userRepository.isUserActiveInTenant(loginRequest.getUsername(), loginRequest.getTenantId())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("error", "User is not active in the specified tenant!"));
		}

		// Set the tenant context
		TenantContext.setCurrentTenant(loginRequest.getTenantId());

		try {
			// Authenticate the user with the provided username and password
			Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
					loginRequest.getUsername(),
					loginRequest.getPassword()
				)
			);

			// Set the authentication in the security context
			SecurityContextHolder.getContext().setAuthentication(authentication);

			// Generate JWT token based on the authentication
			String jwt = jwtUtils.generateJwtToken(authentication);

			// Generate refresh token
			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
			RefreshToken refreshToken = refreshTokenService.generateRefreshToken(
				userDetails.getUsername(), 
				loginRequest.getTenantId()
			);

			// Return a response containing the JWT and refresh token
			return ResponseEntity.ok(new RefreshTokenResponse("success", jwt, refreshToken.getToken(), "User signed in successfully!"));
		} catch (Exception e) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("error", "Invalid username or password!"));
		} finally {
			// Clean up the tenant context
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

		// Generate tenantId from tenant name
		String tenantId = TenantUtils.generateTenantId(signUpRequest.getTenantName());

		// Verify if the tenant already exists
		if (tenantService.existsByTenantId(tenantId)) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("error", "The tenant already exists. Try another name."));
		}

		// Verify if the email is already taken
		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("error", "Email is already taken."));
		}

		try {
			// Get current timestamp
			String now = Instant.now().toString();

			// Create the new tenant
			Tenant tenant = new Tenant(
				signUpRequest.getTenantName(),
				tenantId, 
				now, // createdAt
				now // updatedAt
			);
			tenant = tenantService.createTenant(tenant);

			// Set the tenant context
			TenantContext.setCurrentTenant(tenantId);

			// Create a new user's account
			User user = new User(
				signUpRequest.getUsername(),
				signUpRequest.getFirstName(),
				signUpRequest.getLastName(),
				signUpRequest.getEmail(),
				encoder.encode(signUpRequest.getPassword()),
				tenantId,
				now, // createdAt
				now  // updatedAt
			);
            userRepository.save(user);

			// Initialize default permissions if necessary
			permissionService.initializeDefaultPermissions();

			// Create UserTenantRole assignment for the new user as admin of this tenant
			userTenantRoleService.assignRolesToUser(user.getId(), tenantId, Set.of("admin"));

			// Autenticate the new user
			Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
					signUpRequest.getUsername(),
					signUpRequest.getPassword()
				)
			);

			// Set the authentication in the security context
			SecurityContextHolder.getContext().setAuthentication(authentication);

			// Generate JWT token based on the authentication
			String jwt = jwtUtils.generateJwtToken(authentication);

			// Generate refresh token
			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
			RefreshToken refreshToken = refreshTokenService.generateRefreshToken(
				userDetails.getUsername(), 
				tenantId
			);

			// Return a response containing the JWT and refresh token
			return ResponseEntity.ok(new RefreshTokenResponse("success", jwt, refreshToken.getToken(), "User registered successfully!"));
		} catch (Exception e) {
			// Return an error response if something goes wrong
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("error", "User registration failed! " + e.getMessage()));
		} finally {
			// Clean up the tenant context
			TenantContext.clear();
		}
	}


	/**
	 * Get all information of the user authenticated
	 * 
	 * @return ResponseEntity with the user information or error message.
	 */
	@GetMapping("/me")
	public ResponseEntity<?> getCurrentUser() {

		// Get the current user from the security context
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("error", "Unauthenticated user"));
		}

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		
		UserDetailsResponse response = new UserDetailsResponse(
			userDetails.getId(),
			userDetails.getUsername(),
			userDetails.getFirstName(),
			userDetails.getLastName(),
			userDetails.getEmail(),
			userDetails.getTenantId(),
			userDetails.getAuthorities().stream().map(a -> a.getAuthority()).toList(),
			userDetails.getProfilePictureUrl(),
			userDetails.isActive(),
			userDetails.getCreatedAt(),
			userDetails.getUpdatedAt()
		);

		return ResponseEntity.ok(response);
	}


	/**
	 * Obtain effective permissions from the authenticated user for their current tenant.
	 * 
	 * @return ResponseEntity with the list of permissions or error message.
	 */
	@GetMapping("/me/permissions")
	public ResponseEntity<?> getMyPermissions() {

		// Get the current user from the security context
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("error", "Unauthenticated user"));
		}

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		String userId = userDetails.getId();
		String tenantId = userDetails.getTenantId();

		// Get user roles in the current tenant
		var utrOpt = userTenantRoleService.getUserRolesInTenant(userId, tenantId);
		if (utrOpt.isEmpty()) {
			return ResponseEntity.ok(List.of());
		}

		// Get distinct permissions from all roles
		var permissions = utrOpt.get().getRoles().stream()
				.flatMap(r -> r.getPermissions().stream())
				.map(p -> p.getCode())
				.distinct()
				.toList();

		return ResponseEntity.ok(permissions);
	}


	/**
	 * Change the current tenant of the authenticated user.
	 *
	 * @param switchRequest The request for tenant change (tenantId).
	 * @return ResponseEntity with the new JWT or error message.
	 */
	@PostMapping("/switch-tenant")
	public ResponseEntity<?> switchTenant(@Valid @RequestBody SwitchTenantRequest switchRequest) {
		
		// Get the current user from the security context
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("error", "Unauthenticated user"));
		}

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		String username = userDetails.getUsername();

		// Verify that the user exists in the new tenant
		if (!userTenantService.userExistsInTenant(username, switchRequest.getTenantId())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("error", "The user does not exist in that tenant."));
		}

		// Set the context for the new tenant
		TenantContext.setCurrentTenant(switchRequest.getTenantId());

		try {
			// Authenticate the user in the new tenant
			Authentication newAuthentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(username, userDetails.getPassword()));

			// Set the authentication in the security context
			SecurityContextHolder.getContext().setAuthentication(newAuthentication);

			// Generate new JWT token with the new tenant
			String jwt = jwtUtils.generateJwtToken(newAuthentication);

			// Generate refresh token for the new tenant
			RefreshToken refreshToken = refreshTokenService.generateRefreshToken(
				username, 
				switchRequest.getTenantId()
			);

			// Return a response containing the new JWT, refresh token and user details
			return ResponseEntity.ok(new RefreshTokenResponse("success", jwt, refreshToken.getToken(), "Tenant switched successfully"));
		} catch (Exception e) {
			// Return an error response if something goes wrong
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("error", "Failed to switch tenant. " + e.getMessage()));
		} finally {
			// Clean up the tenant context
			TenantContext.clear();
		}
	}


	/**
	 * Get all tenants available for the authenticated user.
	 *
	 * @return ResponseEntity with the list of tenants.
	 */
	@GetMapping("/user-tenants")
	public ResponseEntity<?> getUserTenants() {
		
		// Get the current user from the security context
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("error", "Unauthenticated user"));
		}

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		String username = userDetails.getUsername();

		// Get all tenants where the user has an account
		List<Tenant> userTenants = userTenantService.getTenantsForUser(username);

		return ResponseEntity.ok(userTenants);
	}

	
	/**
	 * Log out the user and revoke tokens.
	 *
	 * @return ResponseEntity indicating success or error message.
	 */
	@PostMapping("/signout")
	public ResponseEntity<?> signout() {
		try {
			// Get the current user from the security context
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !authentication.isAuthenticated()) {
				return ResponseEntity
						.badRequest()
						.body(new MessageResponse("error", "Unauthenticated user"));
			}

			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
			String username = userDetails.getUsername();
			String tenantId = userDetails.getTenantId();

			// Revoke all user refresh tokens
			refreshTokenService.revokeAllUserTokens(username, tenantId);

			// Revoke all user tokens on the blacklist
			tokenBlacklistService.revokeAllUserTokens(username, tenantId);

			// Clear the security context
			SecurityContextHolder.clearContext();

			return ResponseEntity.ok(new MessageResponse("success", "Session successfully closed"));
		} catch (Exception e) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("error", "Error while logging out: " + e.getMessage()));
		}
	}


	/**
	 * Renew tokens using a refresh token for a specific tenant.
	 * This endpoint is more secure for multitenant systems
	 * as it requires the refresh token to belong to the specified tenant.
	 *
	 * @param refreshTokenRequest The request with the refresh token.
	 * @param tenantId The tenant ID for which to renew the token.
	 * @return ResponseEntity with new tokens or error message.
	 */
	@PostMapping("/refresh/{tenantId}")
	public ResponseEntity<?> refreshTokenForTenant(
		@Valid @RequestBody RefreshTokenRequest refreshTokenRequest,
		@PathVariable String tenantId) {
		try {
			String refreshToken = refreshTokenRequest.getRefreshToken();

			// Validate that the refresh token belongs to the specified tenant
			if (!refreshTokenService.validateRefreshTokenForTenant(refreshToken, tenantId)) {
				return ResponseEntity
						.badRequest()
						.body(new MessageResponse("error", 
							"Refresh token is invalid, expired, or does not belong to the tenant: " + tenantId));
			}

			// Get the refresh token from the database
			Optional<RefreshToken> tokenOptional = 
				refreshTokenService.getRefreshToken(refreshToken);
			
			if (!tokenOptional.isPresent()) {
				return ResponseEntity
						.badRequest()
						.body(new MessageResponse("error", "Refresh token not found"));
			}

			RefreshToken token = tokenOptional.get();

			// Set the tenant context
			TenantContext.setCurrentTenant(tenantId);

			try {
				// Authenticate the user in the specified tenant
				Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
						token.getUsername(),
						null // We do not need the password to refresh.
					)
				);

				// Establishing authentication in the security context
				SecurityContextHolder.getContext().setAuthentication(authentication);

				// Generate a new JWT for the specified tenant
				String newJwt = jwtUtils.generateJwtToken(authentication);

				// Revoke the current refresh token
				refreshTokenService.revokeRefreshToken(refreshToken);

				// Generate a new refresh token for the specified tenant
				RefreshToken newRefreshToken = refreshTokenService.generateRefreshToken(token.getUsername(), tenantId);

				// Return response with new tokens
				return ResponseEntity.ok(new RefreshTokenResponse(
					"success", 
					newJwt, 
					newRefreshToken.getToken(), 
					"Tokens successfully renewed for the tenant: " + tenantId
				));
			} finally {
				// Clean up the security and tenant contexts
				TenantContext.clear();
			}
		} catch (Exception e) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("error", "Error renewing tokens: " + e.getMessage()));
		}
	}
}
