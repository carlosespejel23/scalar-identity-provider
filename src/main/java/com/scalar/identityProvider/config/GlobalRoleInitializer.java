package com.scalar.identityProvider.config;

import com.scalar.identityProvider.services.GlobalRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Componente para inicializar los roles globales al arrancar la aplicación.
 */
@Component
public class GlobalRoleInitializer implements CommandLineRunner {

    @Autowired
    private GlobalRoleService globalRoleService;

    @Override
    public void run(String... args) throws Exception {
        // Inicializar roles globales al arrancar la aplicación
        globalRoleService.initializeGlobalRoles();
        System.out.println("✅ Roles globales inicializados correctamente");
    }
}
