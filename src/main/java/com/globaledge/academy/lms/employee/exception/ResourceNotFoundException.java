package com.globaledge.academy.lms.employee.exception;

/**
 * Custom runtime exception thrown when a requested resource
 * (e.g., an entity from the database) cannot be found.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}