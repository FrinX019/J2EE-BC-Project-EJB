package com.project.service;

import com.project.model.Job;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class JobService {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    // CREATE
    public Job createJob(Job job) {
        job.setStatus(Job.JobStatus.AVAILABLE);
        em.persist(job);
        return job;
    }

    // READ ALL
    public List<Job> findAll() {
        return em.createNamedQuery("Job.findAll", Job.class).getResultList();
    }

    // READ AVAILABLE (for workers to browse)
    public List<Job> findAvailable() {
        return em.createNamedQuery("Job.findAvailable", Job.class).getResultList();
    }

    // READ BY ID
    public Job findById(Long id) {
        return em.find(Job.class, id);
    }

    // READ BY STATUS
    public List<Job> findByStatus(Job.JobStatus status) {
        return em.createNamedQuery("Job.findByStatus", Job.class)
                .setParameter("status", status)
                .getResultList();
    }

    // READ JOBS POSTED BY A USER
    public List<Job> findByPostedBy(String username) {
        return em.createNamedQuery("Job.findByPostedBy", Job.class)
                .setParameter("postedBy", username)
                .getResultList();
    }

    // READ JOBS ACCEPTED BY A WORKER
    public List<Job> findByAcceptedBy(String username) {
        return em.createNamedQuery("Job.findByAcceptedBy", Job.class)
                .setParameter("acceptedByUsername", username)
                .getResultList();
    }

    // UPDATE
    public Job updateJob(Long id, Job updated) {
        Job existing = em.find(Job.class, id);
        if (existing == null)
            return null;
        // Can only update basic info if still AVAILABLE
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setLocation(updated.getLocation());
        existing.setPayRate(updated.getPayRate());
        existing.setCategory(updated.getCategory());
        return em.merge(existing);
    }

    // ACCEPT JOB - uses PESSIMISTIC_WRITE lock for concurrency control
    // (first-come-first-served)
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String acceptJob(Long jobId, String workerUsername) {
        Job job = em.find(Job.class, jobId, LockModeType.PESSIMISTIC_WRITE);
        if (job == null) {
            return "Job not found.";
        }
        if (job.getStatus() != Job.JobStatus.AVAILABLE) {
            return "Job is no longer available. It may have been accepted by another worker.";
        }
        job.setStatus(Job.JobStatus.ACCEPTED);
        job.setAcceptedByUsername(workerUsername);
        job.setAcceptedAt(LocalDateTime.now());
        em.merge(job);
        return "Job accepted successfully by " + workerUsername;
    }

    // CLOSE JOB (posted user can close it)
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String closeJob(Long jobId, String requestedByUsername) {
        Job job = em.find(Job.class, jobId, LockModeType.PESSIMISTIC_WRITE);
        if (job == null)
            return "Job not found.";
        if (!job.getPostedBy().equals(requestedByUsername)) {
            return "Only the job poster can close this job.";
        }
        job.setStatus(Job.JobStatus.CLOSED);
        em.merge(job);
        return "Job closed successfully.";
    }

    // DELETE
    public boolean deleteJob(Long id) {
        Job job = em.find(Job.class, id);
        if (job == null)
            return false;
        em.remove(job);
        return true;
    }
}
