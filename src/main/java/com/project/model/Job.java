package com.project.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@NamedQueries({
        @NamedQuery(name = "Job.findAll", query = "SELECT j FROM Job j ORDER BY j.createdAt DESC"),
        @NamedQuery(name = "Job.findAvailable", query = "SELECT j FROM Job j WHERE j.status = com.project.model.Job.JobStatus.AVAILABLE ORDER BY j.createdAt DESC"),
        @NamedQuery(name = "Job.findByStatus", query = "SELECT j FROM Job j WHERE j.status = :status ORDER BY j.createdAt DESC"),
        @NamedQuery(name = "Job.findByPostedBy", query = "SELECT j FROM Job j WHERE j.postedBy = :postedBy ORDER BY j.createdAt DESC"),
        @NamedQuery(name = "Job.findByAcceptedBy", query = "SELECT j FROM Job j WHERE j.acceptedByUsername = :acceptedByUsername ORDER BY j.acceptedAt DESC")
})
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "location", length = 200)
    private String location;

    @Column(name = "pay_rate", length = 100)
    private String payRate;

    @Column(name = "category", length = 100)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private JobStatus status = JobStatus.AVAILABLE;

    @Column(name = "posted_by", nullable = false, length = 100)
    private String postedBy;

    @Column(name = "accepted_by_username", length = 100)
    private String acceptedByUsername;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    public enum JobStatus {
        AVAILABLE, ACCEPTED, CLOSED
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public Job() {
    }

    public Job(String title, String description, String location, String payRate, String category, String postedBy) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.payRate = payRate;
        this.category = category;
        this.postedBy = postedBy;
        this.status = JobStatus.AVAILABLE;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPayRate() {
        return payRate;
    }

    public void setPayRate(String payRate) {
        this.payRate = payRate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getAcceptedByUsername() {
        return acceptedByUsername;
    }

    public void setAcceptedByUsername(String acceptedByUsername) {
        this.acceptedByUsername = acceptedByUsername;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(LocalDateTime acceptedAt) {
        this.acceptedAt = acceptedAt;
    }
}
