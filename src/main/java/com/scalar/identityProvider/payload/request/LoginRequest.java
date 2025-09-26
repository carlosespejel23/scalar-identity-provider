package com.scalar.identityProvider.payload.request;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

/*
 * Request payload for user login
 */
public class LoginRequest {

	/*
     * Username of the user
     */
	@NotBlank
	@Getter
    @Setter
	private String username;

	/*
	 * Password of the user
	 */
	@NotBlank
	@Getter
    @Setter
	private String password;

	/*
	 * Tenant ID for the login context
	 */
	@NotBlank
	@Getter
    @Setter
	private String tenantId;
}
