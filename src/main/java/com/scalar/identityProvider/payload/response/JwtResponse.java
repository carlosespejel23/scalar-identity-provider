package com.scalar.identityProvider.payload.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/*
 * Response payload for JWT authentication
 */
public class JwtResponse {

	/*
	 * Access token for the authenticated user
	 */
	@Getter
  	@Setter
	private String accessToken;

	/*
	 * Type of the token, typically "Bearer"
	 */
	@Getter
  	@Setter
	private String tokenType = "Bearer";

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
	 * Email of the user
	 */
	@Getter
	@Setter
	private String email;

	/*
	 * Roles assigned to the user
	 */
	@Getter
	@Setter
	private List<String> roles;

	public JwtResponse(String accessToken, String id, String username, String email, List<String> roles) {
		this.accessToken = accessToken;
		this.id = id;
		this.username = username;
		this.email = email;
		this.roles = roles;
	}
}
