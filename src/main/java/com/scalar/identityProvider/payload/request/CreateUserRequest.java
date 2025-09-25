package com.scalar.identityProvider.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

/*
 * Request payload for creating a new user
 */
public class CreateUserRequest {

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

    @NotBlank
    @Size(max = 50)
    @Email
    @Getter
    @Setter
    private String email;

    /*
     * Email of the user
     */
    @NotBlank
    @Size(min = 6, max = 40)
    @Getter
    @Setter
    private String password;

    /*
     * Roles assigned to the user
     */
    @Getter
    @Setter
    private Set<String> roles;


    /*
     * Default constructor
     */
    public CreateUserRequest() {
    }

    /*
     * Parameterized constructor
     */
    public CreateUserRequest(String username, String firstName, String lastName, String email, String password, Set<String> roles) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }
}
