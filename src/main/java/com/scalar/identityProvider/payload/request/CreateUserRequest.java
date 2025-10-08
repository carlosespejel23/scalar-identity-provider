package com.scalar.identityProvider.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

/**
 * Request payload for creating a new user
 * 
 * @param username  The username of the user
 * @param firstName The first name of the user
 * @param lastName  The last name of the user
 * @param email     The email of the user
 * @param password  The password of the user
 * @param tenantId  The ID of the tenant
 * @param roles (optional) The roles assigned to the user
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
     * Tenant ID to which the user is being added
     */
    @NotBlank
    @Size(max = 20)
    @Getter
    @Setter
    private String tenantId;

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

    /**
     * Parameterized constructor
     * 
     * @param username  the username of the user
     * @param firstName the first name of the user
     * @param lastName  the last name of the user
     * @param email     the email of the user
     * @param password  the password of the user
     * @param tenantId  the ID of the tenant
     * @param roles     the roles assigned to the user
     */
    public CreateUserRequest(String username, String firstName, String lastName, String email, String password, String tenantId, Set<String> roles) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.tenantId = tenantId;
        this.roles = roles;
    }
}
