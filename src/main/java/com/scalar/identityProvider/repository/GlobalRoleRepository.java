package com.scalar.identityProvider.repository;

import com.scalar.identityProvider.models.EmployeeRole;
import com.scalar.identityProvider.models.GlobalRole;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Repository for database operations related to GlobalRole.
 */
public interface GlobalRoleRepository extends MongoRepository<GlobalRole, String> {

    /**
     * Search for a global role by name.
     *
     * @param name The name of the role.
     * @return An Optional containing the GlobalRole if found.
     */
    Optional<GlobalRole> findByName(EmployeeRole name);

    /**
     * Check if a global role exists by its name.
     *
     * @param name The name of the role.
     * @return true if it exists, false otherwise.
     */
    boolean existsByName(EmployeeRole name);
}
