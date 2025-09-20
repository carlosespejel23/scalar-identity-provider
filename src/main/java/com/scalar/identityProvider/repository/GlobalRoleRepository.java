package com.scalar.identityProvider.repository;

import com.scalar.identityProvider.models.EmployeeRole;
import com.scalar.identityProvider.models.GlobalRole;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Repositorio para operaciones de base de datos relacionadas con GlobalRole.
 */
public interface GlobalRoleRepository extends MongoRepository<GlobalRole, String> {

    /**
     * Busca un rol global por su nombre.
     *
     * @param name El nombre del rol.
     * @return Un Optional que contiene el GlobalRole si se encuentra.
     */
    Optional<GlobalRole> findByName(EmployeeRole name);

    /**
     * Verifica si un rol global existe por su nombre.
     *
     * @param name El nombre del rol.
     * @return true si existe, false en caso contrario.
     */
    boolean existsByName(EmployeeRole name);
}
