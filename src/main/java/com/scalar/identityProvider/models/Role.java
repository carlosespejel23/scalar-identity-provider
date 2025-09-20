package com.scalar.identityProvider.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "roles")
public class Role {
  @Id
  private String id;

  private EmployeeRole name;

  @NotBlank
  @Size(max = 50)
  private String tenantId;

  public Role() {

  }

  public Role(EmployeeRole name) {
    this.name = name;
  }

  public Role(EmployeeRole name, String tenantId) {
    this.name = name;
    this.tenantId = tenantId;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public EmployeeRole getName() {
    return name;
  }

  public void setName(EmployeeRole name) {
    this.name = name;
  }

  public String getTenantId() {
    return tenantId;
  }

  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }
}
