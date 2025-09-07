package com.employeeservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OpaAuthorizationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(OpaAuthorizationFilter.class);
    private final OpaClient opaClient;

    public OpaAuthorizationFilter(OpaClient opaClient) {
        this.opaClient = opaClient;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Skip authorization for non-API endpoints
        if (!request.getRequestURI().startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Ensure there is an authenticated user before proceeding
        if (SecurityContextHolder.getContext().getAuthentication() == null ||
                !(SecurityContextHolder.getContext().getAuthentication() instanceof JwtAuthenticationToken)) {
            logger.warn("No JWT authentication found for request: {} {}", request.getMethod(), request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = authentication.getToken();
        Map<String, Object> claims = jwt.getClaims();

        // Extract realm roles - GET THE RAW ROLES WITHOUT "ROLE_" PREFIX
        Map<String, Object> realmAccess = (Map<String, Object>) claims.getOrDefault("realm_access", new HashMap<>());
        List<String> roles = (List<String>) realmAccess.getOrDefault("roles", List.of());

        // Build the input map for the OPA query
        Map<String, Object> input = new HashMap<>();
        input.put("method", request.getMethod());
        // Split the path and remove the leading empty string
        List<String> pathList = Arrays.stream(request.getRequestURI().split("/"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        input.put("path", pathList);

        Map<String, Object> user = new HashMap<>();
        user.put("roles", roles);
        user.put("department", claims.getOrDefault("department", ""));
        user.put("employeeId", claims.getOrDefault("employeeId", ""));
        user.put("username", claims.getOrDefault("preferred_username", ""));
        input.put("user", user);

        // Log the request for debugging
        logger.info("OPA Authorization Request:");
        logger.info("Method: {}", request.getMethod());
        logger.info("Path: {}", pathList);
        logger.info("User roles: {}", roles);
        logger.info("Employee ID: {}", claims.getOrDefault("employeeId", ""));
        logger.info("Full input: {}", input);

        try {
            // block() is acceptable here as the security filter chain is synchronous
            boolean isAllowed = opaClient.isAllowed(input).block();
            logger.info("OPA Decision: {}", isAllowed);

            if (isAllowed) {
                filterChain.doFilter(request, response);
            } else {
                logger.warn("Access denied for user {} to {} {}",
                        claims.getOrDefault("preferred_username", "unknown"),
                        request.getMethod(),
                        request.getRequestURI());
                // If OPA denies access, send a 403 Forbidden response
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied by authorization policy.");
            }
        } catch (Exception e) {
            logger.error("Error calling OPA service", e);
            // Fail closed - deny access if OPA call fails
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Authorization service error.");
        }
    }
}