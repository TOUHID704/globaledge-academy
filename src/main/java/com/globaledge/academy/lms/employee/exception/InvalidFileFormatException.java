package com.globaledge.academy.lms.employee.exception;

/**
 * Custom runtime exception thrown when an uploaded file is invalid.
 * This can be due to an incorrect file extension, missing columns, or other structural issues.
 */
public class InvalidFileFormatException extends RuntimeException {

    public InvalidFileFormatException(String message) {
        super(message);
    }
}