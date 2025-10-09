package com.scalar.identityProvider.repository;

import com.scalar.identityProvider.models.User;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for accessing User entities in the MongoDB database.
 * It extends MongoRepository, providing CRUD operations for User objects.
 */
public interface UserRepository extends MongoRepository<User, String> {

  /**
   * Find all Users by their username across all tenants.
   *
   * @param username The username of the user.
   * @return A List containing all Users with that username.
   */
  List<User> findByUsername(String username);

  /**
   * Find all Users by their email across all tenants.
   *
   * @param email The email of the user.
   * @return A List containing all Users with that email.
   */
  List<User> findByEmail(String email);

  /**
   * Check if a username already exists in the database.
   *
   * @param username The username to check.
   * @return A Boolean indicating whether the username exists (true) or not (false).
   */
  Boolean existsByUsername(String username);

  /**
   * Check if an email already exists in the database.
   *
   * @param email The email to check.
   * @return A Boolean indicating whether the email exists (true) or not (false).
   */
  Boolean existsByEmail(String email);

  /**
   * Find a User by their username and tenantId.
   *
   * @param username The username of the user.
   * @param tenantId The tenant ID.
   * @return An Optional containing the User if found, or empty if not found.
   */
  Optional<User> findByUsernameAndTenantId(String username, String tenantId);

  /**
   * Check if a username already exists in the database for a specific tenant.
   *
   * @param username The username to check.
   * @param tenantId The tenant ID.
   * @return A Boolean indicating whether the username exists (true) or not (false).
   */
  Boolean existsByUsernameAndTenantId(String username, String tenantId);

  /**
   * Check if an email already exists in the database for a specific tenant.
   *
   * @param email The email to check.
   * @param tenantId The tenant ID.
   * @return A Boolean indicating whether the email exists (true) or not (false).
   */
  Boolean existsByEmailAndTenantId(String email, String tenantId);

  /**
   * Find all Users by their tenantId.
   *
   * @param tenantId The tenant ID.
   * @return A List containing all Users in that tenant.
   */
  List<User> findByTenantId(String tenantId);

  /**
   * Check if a user is active in a specific tenant.
   *
   * @param username The username of the user.
   * @param tenantId The tenant ID.
   * @return A Boolean indicating whether the user is active (true) or not (false).
   */
  User findByUsernameAndTenantIdAndActive(String username, String tenantId, boolean active);

  /**
   * Check if a user is active
   * 
   * @param username The username of the user.
   * @return A Boolean indicating whether the user is active (true) or not (false).
   */
  User findByUsernameAndActive(String username, boolean active);
}
