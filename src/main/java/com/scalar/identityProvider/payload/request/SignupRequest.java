package com.scalar.identityProvider.payload.request;

import jakarta.validation.constraints.*;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;
 
/*
 * Request payload for user signup
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
     * Roles assigned to the user
     */
    @Getter
    @Setter
    private Set<String> roles;
    
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
