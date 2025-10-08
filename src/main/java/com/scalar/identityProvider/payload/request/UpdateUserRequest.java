package com.scalar.identityProvider.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

/**
 * Request payload for updating user information (all fields are optional).
 * 
 * @param firstName         the first name of the user
 * @param lastName          the last name of the user
 * @param email             the email of the user
 * @param password          the new password for the user
 * @param profilePictureUrl the profile picture URL of the user
 * @param roles             the roles to be assigned to the user
 */
public class UpdateUserRequest {

    /*
     * First name of the user
     */
    @Size(max = 30)
    @Getter
    @Setter
    private String firstName;

    /*
     * Last name of the user
     */
    @Size(max = 30)
    @Getter
    @Setter
    private String lastName;

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
     * Profile picture URL of the user
     */
    @Size(max = 200)
    @Getter
    @Setter
    private String profilePictureUrl;

    /*
     * Roles to be assigned to the user
     */
    @Getter
    @Setter
    private Set<String> roles;


    /*
     * Default constructor
     */
    public UpdateUserRequest() {
    }

    /**
     * Parameterized constructor
     * 
     * @param email    the email of the user
     * @param password the new password for the user
     * @param roles    the roles to be assigned to the user
     */
    public UpdateUserRequest(String email, String password, Set<String> roles) {
        this.email = email;
        this.password = password;
        this.roles = roles;
    }
}
