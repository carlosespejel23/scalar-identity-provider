package com.scalar.identityProvider.security;

/**
 * Contexto de tenant para mantener el tenant actual en el hilo de ejecución.
 * Utiliza ThreadLocal para asegurar que cada hilo tenga su propio contexto de tenant.
 */
public class TenantContext {
    
    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();
    
    /**
     * Establece el tenant actual para el hilo de ejecución.
     * 
     * @param tenantId El ID del tenant
     */
    public static void setCurrentTenant(String tenantId) {
        currentTenant.set(tenantId);
    }
    
    /**
     * Obtiene el tenant actual del hilo de ejecución.
     * 
     * @return El ID del tenant actual
     */
    public static String getCurrentTenant() {
        return currentTenant.get();
    }
    
    /**
     * Limpia el contexto del tenant para el hilo de ejecución.
     * Debe llamarse al final de cada request para evitar memory leaks.
     */
    public static void clear() {
        currentTenant.remove();
    }
}
