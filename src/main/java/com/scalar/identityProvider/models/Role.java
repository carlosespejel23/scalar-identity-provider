package com.scalar.identityProvider.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "roles")
public class Role {

  /*
   * Unique identifier for the role
   */
  @Id
  @Getter
  @Setter
  private String id;

  /*
   * Name of the role
   */
  @NotBlank
  @Size(max = 30)
  @Getter
  @Setter
  private EmployeeRole name;

  /*
   * Tenant ID associated with the role
   */
  @NotBlank
  @Size(max = 50)
  @Getter
  @Setter
  private String tenantId;

  /*
   * Status of the role (active/inactive)
   */
  @Getter
  @Setter
  private boolean active = true;

  /*
   * Date when the role was created
   */
  @Getter
  @Setter
  private String createdAt;

  /*
   * Date when the role was last updated
   */
  @Getter
  @Setter
  private String updatedAt;


  /*
   * Default constructor
   */
  public Role() {
  }

  /*
   * Parameterized constructor
   */
  public Role(EmployeeRole name) {
    this.name = name;
  }

  /*
   * Parameterized constructor
   */
  public Role(EmployeeRole name, String tenantId) {
    this.name = name;
    this.tenantId = tenantId;
  }
}
