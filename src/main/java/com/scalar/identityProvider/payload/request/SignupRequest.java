package com.scalar.identityProvider.payload.request;

import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;
 
/**
 * Request payload for user signup
 * 
 * @param username   The username of the user
 * @param firstName  The first name of the user
 * @param lastName   The last name of the user
 * @param email      The email of the user
 * @param password   The password of the user
 * @param tenantName The name of the tenant the user belongs to
 */
public class SignupRequest {

    /*
     * Username of the user
     */
    @NotBlank
    @Size(min = 3, max = 20)
    @Getter
    @Setter
    private String username;
 
    /*
     * First name of the user
     */
    @NotBlank
    @Size(max = 50)
    @Getter
    @Setter
    private String firstName;

    /*
     * Last name of the user
     */
    @NotBlank
    @Size(max = 50)
    @Getter
    @Setter
    private String lastName;

    /*
     * Email of the user
     */
    @NotBlank
    @Size(max = 50)
    @Email
    @Getter
    @Setter
    private String email;
    
    /*
     * Password of the user
     */
    @NotBlank
    @Size(min = 6, max = 40)
    @Getter
    @Setter
    private String password;

    /*
     * Name of the tenant the user belongs to
     */
    @NotBlank
    @Size(max = 50)
    @Getter
    @Setter
    private String tenantName;
}
