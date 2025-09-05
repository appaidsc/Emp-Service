package com.employeeservice.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
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

    private final OpaClient opaClient;

    public OpaAuthorizationFilter(OpaClient opaClient) {
        this.opaClient = opaClient;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Ensure there is an authenticated user before proceeding
        if (SecurityContextHolder.getContext().getAuthentication() == null ||
                !(SecurityContextHolder.getContext().getAuthentication() instanceof JwtAuthenticationToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = authentication.getToken();
        Map<String, Object> claims = jwt.getClaims();

        // Extract realm roles
        Map<String, Object> realmAccess = (Map<String, Object>) claims.getOrDefault("realm_access", new HashMap<>());
        List<String> roles = (List<String>) realmAccess.getOrDefault("roles", List.of());

        // Build the input map for the OPA query
        Map<String, Object> input = new HashMap<>();
        input.put("method", request.getMethod());
        // Split the path and remove the leading empty string
        input.put("path", Arrays.stream(request.getRequestURI().split("/")).filter(s -> !s.isEmpty()).collect(Collectors.toList()));

        Map<String, Object> user = new HashMap<>();
        user.put("roles", roles);
        user.put("department", claims.getOrDefault("department", ""));
        user.put("employeeId", claims.getOrDefault("employeeId", ""));
        input.put("user", user);

        // block() is acceptable here as the security filter chain is synchronous
        boolean isAllowed = opaClient.isAllowed(input).block();

        if (isAllowed) {
            filterChain.doFilter(request, response);
        } else {
            // If OPA denies access, send a 403 Forbidden response
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied by authorization policy.");
        }
    }
}
