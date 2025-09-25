package com.scalar.identityProvider.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

/*
 * Request payload for updating user information
 */
public class UpdateUserRequest {

    /*
     * Email of the user
     */
    @Size(max = 50)
    @Email
    @Getter
    @Setter
    private String email;

    /*
     * New password for the user
     */
    @Size(min = 6, max = 40)
    @Getter
    @Setter
    private String password;

    /*
     * Roles to be assigned to the user
     */
    @Getter
    @Setter
    private Set<String> roles;

    public UpdateUserRequest() {
    }

    public UpdateUserRequest(String email, String password, Set<String> roles) {
        this.email = email;
        this.password = password;
        this.roles = roles;
    }
}
