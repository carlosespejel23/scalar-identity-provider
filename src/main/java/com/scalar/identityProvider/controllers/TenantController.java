package com.scalar.identityProvider.controllers;

import com.scalar.identityProvider.models.Tenant;
import com.scalar.identityProvider.payload.request.TenantRequest;
import com.scalar.identityProvider.payload.response.MessageResponse;
import com.scalar.identityProvider.services.TenantService;
import com.scalar.identityProvider.services.RoleInitializationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tenants")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @Autowired
    private RoleInitializationService roleInitializationService;

    /**
     * Crea un nuevo tenant.
     *
     * @param tenantRequest La petición de creación de tenant.
     * @return ResponseEntity con el resultado de la operación.
     */
    @PostMapping("/create")
    public ResponseEntity<?> createTenant(@Valid @RequestBody TenantRequest tenantRequest) {
        // Verificar si el tenantId ya existe
        if (tenantService.existsByTenantId(tenantRequest.getTenantId())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Tenant ID ya existe!"));
        }

        // Verificar si el nombre ya existe
        if (tenantService.existsByName(tenantRequest.getName())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Nombre de tenant ya existe!"));
        }

        // Crear el nuevo tenant
        Tenant tenant = new Tenant(
                tenantRequest.getName(),
                tenantRequest.getTenantId(),
                tenantRequest.getDescription()
        );

        tenantService.createTenant(tenant);

        // Inicializar roles para el nuevo tenant
        roleInitializationService.initializeRolesForTenant(tenantRequest.getTenantId());

        return ResponseEntity.ok(new MessageResponse("Tenant creado exitosamente!"));
    }

    /**
     * Obtiene todos los tenants activos.
     *
     * @return ResponseEntity con la lista de tenants.
     */
    @GetMapping("/list")
    public ResponseEntity<List<Tenant>> getAllTenants() {
        List<Tenant> tenants = tenantService.findAllActiveTenants();
        return ResponseEntity.ok(tenants);
    }

    /**
     * Obtiene un tenant por su ID.
     *
     * @param id El ID del tenant.
     * @return ResponseEntity con el tenant o mensaje de error.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTenantById(@PathVariable String id) {
        Optional<Tenant> tenant = tenantService.findById(id);
        if (tenant.isPresent()) {
            return ResponseEntity.ok(tenant.get());
        } else {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Tenant no encontrado!"));
        }
    }

    /**
     * Obtiene un tenant por su tenantId.
     *
     * @param tenantId El tenantId del tenant.
     * @return ResponseEntity con el tenant o mensaje de error.
     */
    @GetMapping("/by-tenant-id/{tenantId}")
    public ResponseEntity<?> getTenantByTenantId(@PathVariable String tenantId) {
        Optional<Tenant> tenant = tenantService.findByTenantId(tenantId);
        if (tenant.isPresent()) {
            return ResponseEntity.ok(tenant.get());
        } else {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Tenant no encontrado!"));
        }
    }
}
