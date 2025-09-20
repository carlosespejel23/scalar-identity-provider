package com.scalar.identityProvider.security.jwt;

import java.io.IOException; // Import IOException for handling input/output exceptions

import jakarta.servlet.FilterChain; // Import FilterChain for handling filter chains
import jakarta.servlet.ServletException; // Import ServletException for servlet-related exceptions
import jakarta.servlet.http.HttpServletRequest; // Import HttpServletRequest for handling HTTP requests
import jakarta.servlet.http.HttpServletResponse; // Import HttpServletResponse for handling HTTP responses

import org.slf4j.Logger; // Import Logger for logging errors and information
import org.slf4j.LoggerFactory; // Import LoggerFactory for creating Logger instances
import org.springframework.beans.factory.annotation.Autowired; // Import Autowired for dependency injection
import org.springframework.lang.NonNull; // Import NonNull for null safety
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // Import for creating authentication tokens
import org.springframework.security.core.context.SecurityContextHolder; // Import for managing security context
import org.springframework.security.core.userdetails.UserDetails; // Import for user details
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource; // Import for authentication details
import org.springframework.util.StringUtils; // Import StringUtils for string utility methods
import org.springframework.web.filter.OncePerRequestFilter; // Import OncePerRequestFilter to ensure the filter is applied once per request

import com.scalar.identityProvider.security.services.UserDetailsServiceImpl; // Import custom user details service
import com.scalar.identityProvider.security.TenantContext; // Import TenantContext for tenant management

/**
 * Filter to validate the JWT token and set user authentication in the security context.
 */
public class AuthTokenFilter extends OncePerRequestFilter {

  @Autowired // Automatically inject JwtUtils to handle JWT operations
  private JwtUtils jwtUtils;

  @Autowired // Automatically inject UserDetailsServiceImpl to load user details
  private UserDetailsServiceImpl userDetailsService;

  private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class); // Logger for logging errors

  /**
   * Filter method to process the JWT token and set authentication.
   *
   * @param request The HTTP request.
   * @param response The HTTP response.
   * @param filterChain The filter chain for further processing.
   * @throws ServletException If a servlet-related exception occurs.
   * @throws IOException If an input or output exception occurs.
   */
  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
          throws ServletException, IOException {
    try {
      // Parse and validate the JWT token from the request
      String jwt = parseJwt(request);
      if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
        // Get the username and tenantId from the validated JWT token
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        String tenantId = jwtUtils.getTenantIdFromJwtToken(jwt);

        // Set the tenant context
        TenantContext.setCurrentTenant(tenantId);

        // Load user details from the username
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Create an authentication token with the user details
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());

        // Set additional details from the request
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Set the authentication in the security context
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (Exception e) {
      // Log any errors that occur during authentication
      logger.error("Cannot set user authentication: {}", e);
    } finally {
      // Clear tenant context after request processing
      TenantContext.clear();
    }

    // Continue the filter chain
    filterChain.doFilter(request, response);
  }

  /**
   * Parse the JWT token from the Authorization header.
   *
   * @param request The HTTP request.
   * @return The JWT token if found, or null if not found.
   */
  private String parseJwt(HttpServletRequest request) {
    // Get the Authorization header from the request
    String headerAuth = request.getHeader("Authorization");

    // Check if the header is valid and starts with "Bearer "
    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
      // Extract the JWT token from the header
      return headerAuth.substring(7);
    }

    return null; // Return null if no valid token is found
  }
}
