package com.scalar.identityProvider.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

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
     * Permissions associated with this global role
     */
    @DBRef
    @Getter
    @Setter
    private Set<Permission> permissions = new HashSet<>();


    /*
     * Default constructor
     */
    public GlobalRole() {
    }

    /**
     * Parameterized constructor
     * 
     * @param name         Name of the global role
     * @param description  Description of the global role
     */
    public GlobalRole(EmployeeRole name, String description) {
        this.name = name;
        this.description = description;
    }
}
