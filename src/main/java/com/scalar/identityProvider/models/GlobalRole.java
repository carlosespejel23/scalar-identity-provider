package com.scalar.identityProvider.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "global_roles")
public class GlobalRole {

    /*
     * Unique identifier for the global role
     */
    @Id
    @Getter
    @Setter
    private String id;

    /*
     * Name of the global role
     */
    @NotBlank
    @Size(max = 20)
    @Getter
    @Setter
    private EmployeeRole name;

    /*
     * Description of the global role
     */
    @Getter
    @Setter
    private String description;

    /*
     * Status of the global role (active/inactive)
     */
    @Getter
    @Setter
    private boolean active = true;


    /*
     * Default constructor
     */
    public GlobalRole() {
    }

    /*
     * Parameterized constructor
     */
    public GlobalRole(EmployeeRole name, String description) {
        this.name = name;
        this.description = description;
    }
}
