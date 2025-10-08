package com.scalar.identityProvider.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

 

import lombok.Getter;
import lombok.Setter;

@Document(collection = "users")
public class User {

  /*
   * Unique identifier for the user
   */
  @Id
  @Getter
  @Setter
  private String id;

  /*
   * Username of the user
   */
  @NotBlank
  @Size(max = 20)
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
  @Indexed(unique = true)
  @Getter
  @Setter
  private String email;

  /*
   * Password of the user
   */
  @NotBlank
  @Size(max = 120)
  @Getter
  @Setter
  private String password;

  /*
   * Tenant ID the user belongs to
   */
  @NotBlank
  @Size(max = 50)
  @Getter
  @Setter
  private String tenantId;

  

  /*
   * Profile picture URL of the user
   */
  @Size(max = 200)
  @Getter
  @Setter
  private String profilePictureUrl;

  /*
   * Status of the user (active/inactive)
   */
  @Getter
  @Setter
  private boolean active = true;

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


  /*
   * Default constructor
   */
  public User() {
  }

  /**
   * Parameterized constructors
   * 
   * @param username   The username of the user
   * @param firstName  The first name of the user
   * @param lastName   The last name of the user
   * @param email      The email of the user
   * @param password   The password of the user
   * @param tenantId   The tenant ID the user belongs to
   * @param createdAt  The date when the user was created
   * @param updatedAt  The date when the user was last updated
   */
  public User(String username, String firstName, String lastName, String email, String password, String tenantId, String createdAt, String updatedAt) {
    this.username = username;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.password = password;
    this.tenantId = tenantId;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }
}
