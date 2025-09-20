package com.scalar.identityProvider.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SwitchTenantRequest {
    @NotBlank
    @Size(max = 20)
    private String tenantId;

    public SwitchTenantRequest() {
    }

    public SwitchTenantRequest(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
