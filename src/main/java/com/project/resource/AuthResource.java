// Requirements:
// - POST /api/auth/register : create a new user with password, return JWT
// - POST /api/auth/login    : verify username+password, return JWT
// - No password hashing (plain text comparison per user request)

package com.project.resource;

import com.project.model.AppUser;
import com.project.security.JwtUtil;
import com.project.service.UserService;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @EJB
    private UserService userService;

    /**
     * Register a new user.
     * Expects: { username, password, fullName, email, role }
     * Returns: { token, username, role }
     */
    @POST
    @Path("/register")
    public Response register(AppUser user) {
        if (user.getUsername() == null || user.getPassword() == null
                || user.getFullName() == null || user.getEmail() == null || user.getRole() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"username, password, fullName, email, and role are required\"}")
                    .build();
        }

        // Check if username already exists
        if (userService.findByUsername(user.getUsername()) != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"message\": \"Username already exists\"}")
                    .build();
        }

        AppUser created = userService.createUser(user);
        String token = JwtUtil.generateToken(created.getUsername(), created.getRole().name());

        System.out.println("[AuthResource] User registered: " + created.getUsername());

        return Response.status(Response.Status.CREATED)
                .entity("{\"token\": \"" + token + "\", \"username\": \"" + created.getUsername()
                        + "\", \"role\": \"" + created.getRole().name() + "\"}")
                .build();
    }

    /**
     * Login with username and password.
     * Expects: { username, password }
     * Returns: { token, username, role }
     */
    @POST
    @Path("/login")
    public Response login(AppUser credentials) {
        if (credentials.getUsername() == null || credentials.getPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"username and password are required\"}")
                    .build();
        }

        AppUser user = userService.findByUsername(credentials.getUsername());
        if (user == null || !user.getPassword().equals(credentials.getPassword())) {
            System.out.println("[AuthResource] Login failed for: " + credentials.getUsername());
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\": \"Invalid username or password\"}")
                    .build();
        }

        String token = JwtUtil.generateToken(user.getUsername(), user.getRole().name());

        System.out.println("[AuthResource] User logged in: " + user.getUsername());

        return Response.ok("{\"token\": \"" + token + "\", \"username\": \"" + user.getUsername()
                + "\", \"role\": \"" + user.getRole().name() + "\"}")
                .build();
    }
}
