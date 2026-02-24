package com.project.resource;

import com.project.model.AppUser;
import com.project.service.UserService;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @EJB
    private UserService userService;

    // GET /api/users - Get all users
    @GET
    public Response getAllUsers() {
        List<AppUser> users = userService.findAll();
        return Response.ok(users).build();
    }

    // GET /api/users/{id} - Get user by ID
    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") Long id) {
        AppUser user = userService.findById(id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"User not found\"}")
                    .build();
        }
        return Response.ok(user).build();
    }

    // GET /api/users/username/{username} - Get by username
    @GET
    @Path("/username/{username}")
    public Response getUserByUsername(@PathParam("username") String username) {
        AppUser user = userService.findByUsername(username);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"User not found\"}")
                    .build();
        }
        return Response.ok(user).build();
    }

    // GET /api/users/role/{role} - Get users by role
    @GET
    @Path("/role/{role}")
    public Response getUsersByRole(@PathParam("role") String role) {
        try {
            AppUser.UserRole userRole = AppUser.UserRole.valueOf(role.toUpperCase());
            List<AppUser> users = userService.findByRole(userRole);
            return Response.ok(users).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"Invalid role. Use USER or WORKER\"}")
                    .build();
        }
    }

    // POST /api/users - Create user
    @POST
    public Response createUser(AppUser user) {
        if (user.getUsername() == null || user.getEmail() == null || user.getRole() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"username, email, and role are required\"}")
                    .build();
        }
        AppUser created = userService.createUser(user);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    // PUT /api/users/{id} - Update user
    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") Long id, AppUser user) {
        AppUser updated = userService.updateUser(id, user);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"User not found\"}")
                    .build();
        }
        return Response.ok(updated).build();
    }

    // DELETE /api/users/{id} - Delete user
    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        boolean deleted = userService.deleteUser(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"User not found\"}")
                    .build();
        }
        return Response.ok("{\"message\": \"User deleted successfully\"}").build();
    }
}
