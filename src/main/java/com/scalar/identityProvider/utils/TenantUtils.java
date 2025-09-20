package com.scalar.identityProvider.utils;

import java.util.Locale;

/**
 * Utilidades para manejo de tenants.
 */
public class TenantUtils {

    /**
     * Convierte un nombre de tenant a un tenantId válido.
     * - Convierte a minúsculas
     * - Reemplaza espacios con guiones
     * - Elimina caracteres especiales
     * - Limita la longitud
     *
     * @param tenantName El nombre del tenant
     * @return El tenantId generado
     */
    public static String generateTenantId(String tenantName) {
        if (tenantName == null || tenantName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del tenant no puede estar vacío");
        }

        return tenantName
                .toLowerCase(Locale.ROOT)                    // Convertir a minúsculas
                .trim()                                      // Eliminar espacios al inicio y final
                .replaceAll("\\s+", "-")                     // Reemplazar espacios con guiones
                .replaceAll("[^a-z0-9-]", "")                // Eliminar caracteres especiales excepto guiones
                .replaceAll("-+", "-")                       // Reemplazar múltiples guiones con uno solo
                .replaceAll("^-|-$", "")                     // Eliminar guiones al inicio y final
                .substring(0, Math.min(20, tenantName.length())); // Limitar a 20 caracteres
    }

    /**
     * Valida si un tenantId es válido.
     *
     * @param tenantId El tenantId a validar
     * @return true si es válido, false en caso contrario
     */
    public static boolean isValidTenantId(String tenantId) {
        if (tenantId == null || tenantId.trim().isEmpty()) {
            return false;
        }
        
        // Debe contener solo letras minúsculas, números y guiones
        // No puede empezar o terminar con guión
        // Debe tener entre 3 y 20 caracteres
        return tenantId.matches("^[a-z0-9][a-z0-9-]{1,18}[a-z0-9]$");
    }
}
