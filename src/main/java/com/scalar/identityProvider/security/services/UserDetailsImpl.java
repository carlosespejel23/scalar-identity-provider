package com.scalar.identityProvider.security.services;

import com.scalar.identityProvider.models.User;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collection;
import java.util.Objects;


/**
 * Implementation of Spring Security's UserDetails interface for representing user details.
 */
public class UserDetailsImpl implements UserDetails {
	private static final long serialVersionUID = 1L; // Serializable version identifier

	private String id;
	private String username;
	private String firstName;
	private String lastName;
	private String email;
	private String tenantId;

	@JsonIgnore // Prevent serialization of the password field
	private String password;

	private Collection<? extends GrantedAuthority> authorities; // Collection of user's authorities (roles)

	private String profilePictureUrl;
	private boolean active;
	private String createdAt;
	private String updatedAt;

	
	/**
	 * Constructor to initialize UserDetailsImpl.
	 *
	 * @param id           The unique identifier of the user.
	 * @param username     The username of the user.
	 * @param firstName    The first name of the user.
	 * @param lastName     The last name of the user.
	 * @param email        The email of the user.
	 * @param tenantId     The tenant ID where the user belongs to
	 * @param password     The password of the user.
	 * @param authorities  The collection of user's authorities.
	 * @param profilePictureUrl The URL of the user's profile picture.
	 * @param active       The active status of the user.
	 * @param createdAt    The creation timestamp of the user.
	 * @param updatedAt    The last update timestamp of the user.
	 */
	public UserDetailsImpl(
		String id, 
		String username, 
		String firstName, 
		String lastName,
		String email, 
		String tenantId,
		String password,
		Collection<? extends GrantedAuthority> authorities,
		String profilePictureUrl,
		boolean active,
		String createdAt,
		String updatedAt) {
		this.id = id;
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.tenantId = tenantId;
		this.password = password;
		this.authorities = authorities;
		this.profilePictureUrl = profilePictureUrl;
		this.active = active;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	/**
	 * Builds a UserDetailsImpl instance from a User object.
	 *
	 * @param user The User object.
	 * @return A UserDetailsImpl instance.
	 */
    public static UserDetailsImpl build(User user, Collection<? extends GrantedAuthority> authorities) {
        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getTenantId(),
                user.getPassword(),
                authorities,
                user.getProfilePictureUrl(),
                user.isActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
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

	public String getTenantId() {
		return tenantId; // Return tenant ID
	}

	@Override
	public String getPassword() {
		return password; // Return password
	}

	@Override
	public String getUsername() {
		return username; // Return username
	}

	public String getFirstName() {
		return firstName; // Return first name
	}

	public String getLastName() {
		return lastName; // Return last name
	}

	public String getProfilePictureUrl() {
		return profilePictureUrl; // Return profile picture URL
	}

	public boolean isActive() {
		return active; // Return active status
	}

	public String getCreatedAt() {
		return createdAt; // Return creation timestamp
	}

	public String getUpdatedAt() {
		return updatedAt; // Return last update timestamp
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
