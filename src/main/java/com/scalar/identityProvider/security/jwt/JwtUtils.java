package com.scalar.identityProvider.security.jwt;

import com.scalar.identityProvider.security.services.UserDetailsImpl;
import com.scalar.identityProvider.security.TenantContext;
import com.scalar.identityProvider.services.TokenBlacklistService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;
import java.time.Instant;
import java.util.Date;


/**
 * Utility class for managing JSON Web Tokens (JWT).
 */
@Component
public class JwtUtils {

  private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class); // Logger for logging errors

  @Value("${jwt.secret}") // Inject the JWT secret from application properties
  private String jwtSecret;

  @Value("${jwt.expiration.ms}") // Inject the JWT expiration time from application properties
  private int jwtExpirationMs;

  @Autowired // Inject TokenBlacklistService for blacklist validation
  private TokenBlacklistService tokenBlacklistService;


  /**
   * Generate a JWT token based on the provided authentication.
   *
   * @param authentication The authentication object containing user details.
   * @return The generated JWT token as a string.
   */
  public String generateJwtToken(Authentication authentication) {
    // Get the user details from the authentication object
    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
    
    // Get the current tenant from context
    String tenantId = TenantContext.getCurrentTenant();

    // Build and return the JWT token
    return Jwts.builder()
            .setSubject((userPrincipal.getUsername())) // Set the subject (username)
            .claim("tenantId", tenantId) // Add tenantId as a claim
            .setIssuedAt(new Date()) // Set the issue date
            .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Set the expiration date
            .signWith(key(), SignatureAlgorithm.HS256)
            // Sign the token using the secret key and algorithm
            .compact(); // Compact the JWT into a string
  }


  /**
   * Create a signing key from the JWT secret.
   *
   * @return The signing key as a Key object.
   */
  private Key key() {
    // Decode the JWT secret and create a signing key
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
  }


  /**
   * Extract the username from the given JWT token.
   *
   * @param token The JWT token.
   * @return The username extracted from the token.
   */
  public String getUserNameFromJwtToken(String token) {
    // Parse the JWT token and return the subject (username)
    return Jwts.parserBuilder().setSigningKey(key()).build()
            .parseClaimsJws(token).getBody().getSubject();
  }


  /**
   * Extract the tenantId from the given JWT token.
   *
   * @param token The JWT token.
   * @return The tenantId extracted from the token.
   */
  public String getTenantIdFromJwtToken(String token) {
    // Parse the JWT token and return the tenantId claim
    return Jwts.parserBuilder().setSigningKey(key()).build()
            .parseClaimsJws(token).getBody().get("tenantId", String.class);
  }


  /**
   * Validate the given JWT token.
   *
   * @param authToken The JWT token to validate.
   * @return True if the token is valid, false otherwise.
   */
  public boolean validateJwtToken(String authToken) {
    try {
      // Check if token is in blacklist
      if (tokenBlacklistService.isTokenBlacklisted(authToken)) {
        logger.error("JWT token is blacklisted");
        return false;
      }
      
      // Parse the token and verify its signature
      Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
      return true; // Token is valid
    } catch (MalformedJwtException e) {
      logger.error("Invalid JWT token: {}", e.getMessage()); // Log invalid token error
    } catch (ExpiredJwtException e) {
      logger.error("JWT token is expired: {}", e.getMessage()); // Log expired token error
    } catch (UnsupportedJwtException e) {
      logger.error("JWT token is unsupported: {}", e.getMessage()); // Log unsupported token error
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims string is empty: {}", e.getMessage()); // Log empty claims error
    }

    return false; // Token is invalid
  }


  /**
   * Extract expiration date from JWT token.
   *
   * @param token The JWT token.
   * @return The expiration date as Instant.
   */
  public Instant getExpirationFromJwtToken(String token) {
    return Jwts.parserBuilder().setSigningKey(key()).build()
            .parseClaimsJws(token).getBody().getExpiration().toInstant();
  }

  
  /**
   * Extract all claims from JWT token.
   *
   * @param token The JWT token.
   * @return The claims from the token.
   */
  public Claims getClaimsFromJwtToken(String token) {
    return Jwts.parserBuilder().setSigningKey(key()).build()
            .parseClaimsJws(token).getBody();
  }
}
