package com.project;

import com.project.resource.JobResource;
import com.project.resource.UserResource;
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
        return classes;
    }
}