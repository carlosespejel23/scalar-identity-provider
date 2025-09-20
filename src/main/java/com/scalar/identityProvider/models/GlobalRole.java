package com.scalar.identityProvider.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "global_roles")
public class GlobalRole {
    @Id
    private String id;

    private EmployeeRole name;

    private String description;

    private boolean active = true;

    public GlobalRole() {
    }

    public GlobalRole(EmployeeRole name, String description) {
        this.name = name;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
