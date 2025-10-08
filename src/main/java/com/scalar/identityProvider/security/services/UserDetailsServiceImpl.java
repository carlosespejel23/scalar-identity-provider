package com.scalar.identityProvider.security.services;

import com.scalar.identityProvider.models.User;
import com.scalar.identityProvider.models.GlobalRole;
import com.scalar.identityProvider.services.UserTenantRoleService;
import com.scalar.identityProvider.repository.UserRepository;
import com.scalar.identityProvider.security.TenantContext;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Implementation of UserDetailsService to load user-specific data.
 */
@Service // Indicates that this class is a service component
public class UserDetailsServiceImpl implements UserDetailsService {

	/*
	 * Dependencies
	 */
	UserRepository userRepository;

	UserTenantRoleService userTenantRoleService;

	/**
	 * Constructor for UserDetailsServiceImpl.
	 *
	 * @param userRepository The repository to access user data.
	 * @param userTenantRoleService The service to access user roles within tenants.
	 */
	public UserDetailsServiceImpl(UserRepository userRepository, UserTenantRoleService userTenantRoleService) {
		this.userRepository = userRepository;
		this.userTenantRoleService = userTenantRoleService;
	}

	
	/**
	 * Loads user details by username.
	 *
	 * @param username The username of the user.
	 * @return UserDetails containing user information.
	 * @throws UsernameNotFoundException if the user is not found.
	 */
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// Get the current tenant from context
		String tenantId = TenantContext.getCurrentTenant();
		
		if (tenantId == null) {
			throw new UsernameNotFoundException("Tenant context not found for username: " + username);
		}

		// Attempt to find the user by username and tenantId
		User user = userRepository.findByUsernameAndTenantId(username, tenantId)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username + " and tenant: " + tenantId));

		// Construir authorities desde UserTenantRole para el tenant actual
		Set<GlobalRole> roles = userTenantRoleService
			.getUserRolesInTenant(user.getId(), tenantId)
			.map(r -> r.getRoles())
			.orElse(new HashSet<>());

		Collection<? extends GrantedAuthority> authorities = roles.stream()
			.map(r -> new SimpleGrantedAuthority(r.getName().name()))
			.collect(Collectors.toList());

		// Crear el UserDetails usando las authorities derivadas
		return UserDetailsImpl.build(user, authorities);
	}
}
