package com.scalar.identityProvider.security.services;

import java.util.Collection; // Import Collection for holding authorities
import java.util.List; // Import List for storing roles
import java.util.Objects; // Import Objects for object comparison
import java.util.stream.Collectors; // Import Collectors for stream operations

import com.scalar.identityProvider.models.User; // Import User model
import org.springframework.security.core.GrantedAuthority; // Import GrantedAuthority for user authorities
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Import SimpleGrantedAuthority for role representation
import org.springframework.security.core.userdetails.UserDetails; // Import UserDetails for Spring Security
import com.fasterxml.jackson.annotation.JsonIgnore; // Import JsonIgnore to prevent serialization of sensitive data

/**
 * Implementation of Spring Security's UserDetails interface for representing user details.
 */
public class UserDetailsImpl implements UserDetails {
	private static final long serialVersionUID = 1L; // Serializable version identifier

	private String id; // Unique identifier for the user
	private String username; // Username of the user
	private String email; // Email address of the user

	@JsonIgnore // Prevent serialization of the password field
	private String password; // Password of the user

	private Collection<? extends GrantedAuthority> authorities; // Collection of user's authorities (roles)

	/**
	 * Constructor to initialize UserDetailsImpl.
	 *
	 * @param id           The unique identifier of the user.
	 * @param username     The username of the user.
	 * @param email        The email of the user.
	 * @param password     The password of the user.
	 * @param authorities  The collection of user's authorities.
	 */
	public UserDetailsImpl(String id, String username, String email, String password,
						   Collection<? extends GrantedAuthority> authorities) {
		this.id = id; // Set user ID
		this.username = username; // Set username
		this.email = email; // Set email
		this.password = password; // Set password
		this.authorities = authorities; // Set authorities
	}

	/**
	 * Builds a UserDetailsImpl instance from a User object.
	 *
	 * @param user The User object.
	 * @return A UserDetailsImpl instance.
	 */
	public static UserDetailsImpl build(User user) {
		// Map the roles of the user to GrantedAuthority
		List<GrantedAuthority> authorities = user.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority(role.getName().name())) // Convert each role to SimpleGrantedAuthority
				.collect(Collectors.toList()); // Collect into a list

		// Return a new UserDetailsImpl object
		return new UserDetailsImpl(
				user.getId(), // User ID
				user.getUsername(), // Username
				user.getEmail(), // Email
				user.getPassword(), // Password
				authorities); // User authorities
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities; // Return user's authorities
	}

	public String getId() {
		return id; // Return user ID
	}

	public String getEmail() {
		return email; // Return email
	}

	@Override
	public String getPassword() {
		return password; // Return password
	}

	@Override
	public String getUsername() {
		return username; // Return username
	}

	@Override
	public boolean isAccountNonExpired() {
		return true; // Account is not expired
	}

	@Override
	public boolean isAccountNonLocked() {
		return true; // Account is not locked
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true; // Credentials are not expired
	}

	@Override
	public boolean isEnabled() {
		return true; // Account is enabled
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) // Check if the same object
			return true;
		if (o == null || getClass() != o.getClass()) // Check if the object is null or not of the same class
			return false;
		UserDetailsImpl user = (UserDetailsImpl) o; // Cast to UserDetailsImpl
		return Objects.equals(id, user.id); // Check if IDs are equal
	}
}
