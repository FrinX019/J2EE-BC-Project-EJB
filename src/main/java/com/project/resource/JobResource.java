package com.project.resource;

import com.project.model.Job;
import com.project.service.JobService;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/jobs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JobResource {

    @EJB
    private JobService jobService;

    // GET /api/jobs - Get all jobs
    @GET
    public Response getAllJobs() {
        List<Job> jobs = jobService.findAll();
        return Response.ok(jobs).build();
    }

    // GET /api/jobs/available - Get all available jobs (for workers)
    @GET
    @Path("/available")
    public Response getAvailableJobs() {
        List<Job> jobs = jobService.findAvailable();
        return Response.ok(jobs).build();
    }

    // GET /api/jobs/{id} - Get specific job
    @GET
    @Path("/{id}")
    public Response getJobById(@PathParam("id") Long id) {
        Job job = jobService.findById(id);
        if (job == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"Job not found\"}")
                    .build();
        }
        return Response.ok(job).build();
    }

    // GET /api/jobs/status/{status} - Get jobs by status
    @GET
    @Path("/status/{status}")
    public Response getJobsByStatus(@PathParam("status") String status) {
        try {
            Job.JobStatus jobStatus = Job.JobStatus.valueOf(status.toUpperCase());
            List<Job> jobs = jobService.findByStatus(jobStatus);
            return Response.ok(jobs).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"Invalid status. Use AVAILABLE, ACCEPTED, or CLOSED\"}")
                    .build();
        }
    }

    // GET /api/jobs/posted/{username} - Get jobs posted by a user
    @GET
    @Path("/posted/{username}")
    public Response getJobsByPoster(@PathParam("username") String username) {
        List<Job> jobs = jobService.findByPostedBy(username);
        return Response.ok(jobs).build();
    }

    // GET /api/jobs/accepted/{username} - Get jobs accepted by a worker
    @GET
    @Path("/accepted/{username}")
    public Response getJobsAcceptedByWorker(@PathParam("username") String username) {
        List<Job> jobs = jobService.findByAcceptedBy(username);
        return Response.ok(jobs).build();
    }

    // POST /api/jobs - Create a new job (by USER role)
    @POST
    public Response createJob(Job job) {
        if (job.getTitle() == null || job.getDescription() == null || job.getPostedBy() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"title, description, and postedBy are required\"}")
                    .build();
        }
        Job created = jobService.createJob(job);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    // PUT /api/jobs/{id} - Update a job
    @PUT
    @Path("/{id}")
    public Response updateJob(@PathParam("id") Long id, Job job) {
        Job updated = jobService.updateJob(id, job);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"Job not found\"}")
                    .build();
        }
        return Response.ok(updated).build();
    }

    // PUT /api/jobs/{id}/accept - Accept a job (by WORKER role) - with concurrency
    // control
    @PUT
    @Path("/{id}/accept")
    public Response acceptJob(@PathParam("id") Long id, @QueryParam("worker") String workerUsername) {
        if (workerUsername == null || workerUsername.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"Worker username is required as query param ?worker=username\"}")
                    .build();
        }
        String result = jobService.acceptJob(id, workerUsername);
        if (result.startsWith("Job accepted")) {
            return Response.ok("{\"message\": \"" + result + "\"}").build();
        } else {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"message\": \"" + result + "\"}")
                    .build();
        }
    }

    // PUT /api/jobs/{id}/close - Close a job (by poster)
    @PUT
    @Path("/{id}/close")
    public Response closeJob(@PathParam("id") Long id, @QueryParam("user") String username) {
        if (username == null || username.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"User username is required as query param ?user=username\"}")
                    .build();
        }
        String result = jobService.closeJob(id, username);
        if (result.startsWith("Job closed")) {
            return Response.ok("{\"message\": \"" + result + "\"}").build();
        } else {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"message\": \"" + result + "\"}")
                    .build();
        }
    }

    // DELETE /api/jobs/{id} - Delete a job
    @DELETE
    @Path("/{id}")
    public Response deleteJob(@PathParam("id") Long id) {
        boolean deleted = jobService.deleteJob(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"Job not found\"}")
                    .build();
        }
        return Response.ok("{\"message\": \"Job deleted successfully\"}").build();
    }
}
