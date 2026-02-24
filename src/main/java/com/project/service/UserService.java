package com.project.service;

import com.project.model.AppUser;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class UserService {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    // CREATE
    public AppUser createUser(AppUser user) {
        em.persist(user);
        return user;
    }

    // READ ALL
    public List<AppUser> findAll() {
        return em.createNamedQuery("AppUser.findAll", AppUser.class).getResultList();
    }

    // READ BY ID
    public AppUser findById(Long id) {
        return em.find(AppUser.class, id);
    }

    // READ BY USERNAME
    public AppUser findByUsername(String username) {
        try {
            return em.createNamedQuery("AppUser.findByUsername", AppUser.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    // READ BY ROLE
    public List<AppUser> findByRole(AppUser.UserRole role) {
        return em.createNamedQuery("AppUser.findByRole", AppUser.class)
                .setParameter("role", role)
                .getResultList();
    }

    // UPDATE
    public AppUser updateUser(Long id, AppUser updated) {
        AppUser existing = em.find(AppUser.class, id);
        if (existing == null)
            return null;
        existing.setFullName(updated.getFullName());
        existing.setEmail(updated.getEmail());
        existing.setRole(updated.getRole());
        return em.merge(existing);
    }

    // DELETE
    public boolean deleteUser(Long id) {
        AppUser user = em.find(AppUser.class, id);
        if (user == null)
            return false;
        em.remove(user);
        return true;
    }
}
