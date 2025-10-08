package com.scalar.identityProvider.payload.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class UserDetailsResponse {

	/*
	 * Unique identifier for the user
	 */
	@Getter
	@Setter
	private String id;

	/*
	 * Username of the user
	 */
	@Getter
	@Setter
	private String username;

	/*
	 * First name of the user
	 */
	@Getter
	@Setter
	private String firstName;

	/*
	 * Last name of the user
	 */
	@Getter
	@Setter
	private String lastName;

	/*
	 * Email of the user
	 */
	@Getter
	@Setter
	private String email;

	/*
	 * Tenant ID the user belongs to
	 */
	@Getter
	@Setter
	private String tenantId;

	/*
	 * Roles assigned to the user
	 */
	@Getter
	@Setter
	private List<String> roles;

	/*
     * Profile picture URL of the user
     */
	@Getter
	@Setter
	private String profilePictureUrl;

	/*
     * Status of the user (active/inactive)
     */
	@Getter
	@Setter
	private boolean active;

	/*
	 * Date when the user was created
	 */
	@Getter
	@Setter
	private String createdAt;

	/*
	 * Date when the user was last updated
	 */
	@Getter
	@Setter
	private String updatedAt;


	/**
	 * Parameterized constructor
	 * 
	 * @param id Unique identifier for the user
	 * @param username Username of the user
	 * @param firstName First name of the user
	 * @param lastName Last name of the user
	 * @param email Email of the user
	 * @param tenantId Tenant ID the user belongs to
	 * @param roles Roles assigned to the user
	 * @param profilePictureUrl Profile picture URL of the user
	 * @param active Status of the user (active/inactive)
	 * @param createdAt Date when the user was created
	 * @param updatedAt Date when the user was last updated
	 */
	public UserDetailsResponse(
		String id, 
		String username, 
		String firstName, 
		String lastName, 
		String email, 
		String tenantId,
		List<String> roles,
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
		this.roles = roles;
		this.profilePictureUrl = profilePictureUrl;
		this.active = active;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
}
