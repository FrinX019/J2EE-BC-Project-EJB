package com.project;

import com.project.resource.AuthResource;
import com.project.resource.JobResource;
import com.project.resource.UserResource;
import com.project.security.JwtAuthFilter;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class HelloApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(JobResource.class);
        classes.add(UserResource.class);
        classes.add(AuthResource.class);
        classes.add(JwtAuthFilter.class);
        return classes;
    }
}