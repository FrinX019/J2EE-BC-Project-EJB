// Requirements:
// - JAX-RS ContainerRequestFilter that intercepts all /api requests
// - Skips /api/auth/register and /api/auth/login (public endpoints)
// - Validates Bearer token from Authorization header via JwtUtil
// - Returns 401 if token is missing or invalid
// - Sets "username" and "role" as request properties for downstream resources

package com.project.security;

import io.jsonwebtoken.Claims;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class JwtAuthFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext ctx) throws IOException {
        String path = ctx.getUriInfo().getPath();
        // Strip leading slash for consistent matching
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        // Allow public auth endpoints through
        if (path.startsWith("auth/")) {
            System.out.println("[JwtAuthFilter] Skipping auth for public path: " + path);
            return;
        }

        String authHeader = ctx.getHeaderString("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("[JwtAuthFilter] Missing or malformed Authorization header for: " + path);
            ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\": \"Missing or invalid Authorization header\"}")
                    .build());
            return;
        }

        String token = authHeader.substring(7);
        Claims claims = JwtUtil.parseToken(token);
        if (claims == null) {
            System.out.println("[JwtAuthFilter] Invalid token for: " + path);
            ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\": \"Invalid or expired token\"}")
                    .build());
            return;
        }

        // Make username and role available to downstream resources
        ctx.setProperty("username", claims.getSubject());
        ctx.setProperty("role", claims.get("role", String.class));

        System.out.println("[JwtAuthFilter] Authenticated: " + claims.getSubject() + " role=" + claims.get("role"));
    }
}
