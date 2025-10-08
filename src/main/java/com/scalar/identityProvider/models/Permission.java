package com.scalar.identityProvider.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Getter;
import lombok.Setter;

@Document(collection = "permissions")
public class Permission {

    /*
     * Unique identifier for the permission
     */
    @Id
    @Getter
    @Setter
    private String id;

    /*
     * Code representing the permission
     */
    @NotBlank
    @Size(max = 100)
    @Getter
    @Setter
    private String code; // e.g., VIEW_DASHBOARD, EDIT_USER, COMPONENT:UsersTable

    /*
     * Description of the permission
     */
    @Size(max = 200)
    @Getter
    @Setter
    private String description;

    /*
     * Status of the permission (active/inactive)
     */
    @Getter
    @Setter
    private boolean active = true;


    /*
     * Default constructor
     */
    public Permission() {}

    /**
     * Parameterized constructor
     * 
     * @param code        The code of the permission
     * @param description The description of the permission
     */
    public Permission(String code, String description) {
        this.code = code;
        this.description = description;
    }
}


