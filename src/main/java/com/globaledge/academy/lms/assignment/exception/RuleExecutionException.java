// 🎯 assignment/exception/RuleExecutionException.java
package com.globaledge.academy.lms.assignment.exception;

public class RuleExecutionException extends RuntimeException {
    public RuleExecutionException(String message) {
        super(message);
    }

    public RuleExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}