package com.globaledge.academy.lms.employee.exception;

/**
 * Custom runtime exception thrown when a fatal error occurs during the
 * employee import business logic, after the file has been parsed.
 */
public class EmployeeImportProcessingException extends RuntimeException {

    public EmployeeImportProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}