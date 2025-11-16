// 🎯 assignment/exception/AssignmentRuleNotFoundException.java
package com.globaledge.academy.lms.assignment.exception;

public class AssignmentRuleNotFoundException extends RuntimeException {
    public AssignmentRuleNotFoundException(String message) {
        super(message);
    }
}