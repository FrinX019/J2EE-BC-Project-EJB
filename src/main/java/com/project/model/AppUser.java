package com.project.model;

import jakarta.persistence.*;

@Entity
@Table(name = "app_users")
@NamedQueries({
        @NamedQuery(name = "AppUser.findAll", query = "SELECT u FROM AppUser u"),
        @NamedQuery(name = "AppUser.findByUsername", query = "SELECT u FROM AppUser u WHERE u.username = :username"),
        @NamedQuery(name = "AppUser.findByRole", query = "SELECT u FROM AppUser u WHERE u.role = :role")
})
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true, length = 200)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    public enum UserRole {
        USER, WORKER
    }

    public AppUser() {
    }

    public AppUser(String username, String fullName, String email, UserRole role) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
