package com.scalar.identityProvider.security;

/**
 * Tenant context to keep the current tenant in the execution thread.
 * Use ThreadLocal to ensure that each thread has its own tenant context.
 */
public class TenantContext {
    
    // ThreadLocal to hold the current tenant ID for each thread
    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();
    
    /**
     * Sets the current tenant for the execution thread.
     * 
     * @param tenantId The ID of the tenant to set
     */
    public static void setCurrentTenant(String tenantId) {
        currentTenant.set(tenantId);
    }
    
    /**
     * Gets the current tenant of the thread.
     * 
     * @return The ID of the current tenant
     */
    public static String getCurrentTenant() {
        return currentTenant.get();
    }
    
    /**
     * Cleans the tenant context for the execution thread.
     * It must be called at the end of each request to avoid memory leaks.
     */
    public static void clear() {
        currentTenant.remove();
    }
}
