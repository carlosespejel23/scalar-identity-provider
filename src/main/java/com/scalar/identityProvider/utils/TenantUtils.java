package com.scalar.identityProvider.utils;

import java.util.Locale;


/**
 * Utilities for tenant management.
 */
public class TenantUtils {

    /**
     * Convert a tenant name to a valid tenantId.
     * - Convert to lowercase
     * - Replace spaces with hyphens
     * - Remove special characters
     * - Limit the length
     *
     * @param tenantName The tenant name
     * @return The tenant generated
     */
    public static String generateTenantId(String tenantName) {
        if (tenantName == null || tenantName.trim().isEmpty()) {
            throw new IllegalArgumentException("The tenant name cannot be empty.");
        }

        return tenantName
                .toLowerCase(Locale.ROOT)                    // Convert to lowercase
                .trim()                                      // Remove spaces at the beginning and end
                .replaceAll("\\s+", "-")                     // Replace spaces with hyphens
                .replaceAll("[^a-z0-9-]", "")                // Remove special characters except hyphens
                .replaceAll("-+", "-")                       // Replace multiple hyphens with a single one
                .replaceAll("^-|-$", "")                     // Remove hyphens at the beginning and end
                .substring(0, Math.min(20, tenantName.length())); // Limit to 20 characters
    }


    /**
     * Validates whether a tenantId is valid.
     *
     * @param tenantId The tenantId to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidTenantId(String tenantId) {
        if (tenantId == null || tenantId.trim().isEmpty()) {
            return false;
        }
        
        // It must contain only lowercase letters, numbers, and hyphens.
        // Cannot start or end with a hyphen
        // Must be between 3 and 20 characters long
        return tenantId.matches("^[a-z0-9][a-z0-9-]{1,18}[a-z0-9]$");
    }
}
