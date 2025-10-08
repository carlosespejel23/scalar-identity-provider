package com.scalar.identityProvider.config;

import com.scalar.identityProvider.services.GlobalRoleService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Component to initialize global roles when starting the application.
 */
@Component
public class GlobalRoleInitializer implements CommandLineRunner {

    /*
     * Dependencies
     */
    private GlobalRoleService globalRoleService;

    /**
     * Constructor
     * 
     * @param globalRoleService the global role service
     */
    public GlobalRoleInitializer(GlobalRoleService globalRoleService) {
        this.globalRoleService = globalRoleService;
    }


    /*
     * Initialize global roles on application startup
     */
    @Override
    public void run(String... args) throws Exception {
        globalRoleService.initializeGlobalRoles();
        IO.println("Global roles initialized successfully");
    }
}
