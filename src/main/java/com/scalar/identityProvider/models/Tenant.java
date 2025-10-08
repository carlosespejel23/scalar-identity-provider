package com.scalar.identityProvider.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "tenants")
public class Tenant {

    /*
     * Unique identifier for the tenant
     */
    @Id
    @Getter
    @Setter
    private String id;

    /*
     * Name of the tenant
     */
    @NotBlank
    @Size(max = 50)
    @Getter
    @Setter
    private String name;

    /*
     * Tenant ID
     */
    @NotBlank
    @Size(max = 20)
    @Getter
    @Setter
    private String tenantId;

    /*
     * Description of the tenant
     */
    @Size(max = 200)
    @Getter
    @Setter
    private String description;

    /*
     * Logo URL of the tenant
     */
    @Size(max = 200)
    @Getter
    @Setter
    private String logoUrl;

    /*
     * Status of the tenant (active/inactive)
     */
    @Getter
    @Setter
    private boolean active = true;

    /*
     * Date when the tenant was created
     */
    @Getter
    @Setter
    private String createdAt;

    /*
     * Date when the tenant was updated
     */
    @Getter
    @Setter
    private String updatedAt;


    /*
     * Default constructor
     */
    public Tenant() {
    }

    /**
     * Parameterized constructor
     * 
     * @param name       Name of the tenant
     * @param tenantId   Tenant ID
     * @param createdAt  Date when the tenant was created
     * @param updatedAt  Date when the tenant was updated
     */
    public Tenant(String name, String tenantId, String createdAt, String updatedAt) {
        this.name = name;
        this.tenantId = tenantId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
