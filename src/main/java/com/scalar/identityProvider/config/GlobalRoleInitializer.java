package com.scalar.identityProvider.config;

import com.scalar.identityProvider.services.GlobalRoleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Component to initialize global roles when starting the application.
 */
@Component
public class GlobalRoleInitializer implements CommandLineRunner {

    @Autowired
    private GlobalRoleService globalRoleService;

    @Override
    public void run(String... args) throws Exception {
        globalRoleService.initializeGlobalRoles();
        IO.println("Global roles initialized successfully");
    }
}
