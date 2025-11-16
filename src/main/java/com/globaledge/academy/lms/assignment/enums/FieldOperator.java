// 🎯 assignment/enums/FieldOperator.java
package com.globaledge.academy.lms.assignment.enums;

public enum FieldOperator {
    EQUALS,                // field = value
    NOT_EQUALS,            // field != value
    CONTAINS,              // field LIKE %value%
    NOT_CONTAINS,          // field NOT LIKE %value%
    IN,                    // field IN (value1, value2, ...)
    NOT_IN,                // field NOT IN (value1, value2, ...)
    GREATER_THAN,          // field > value (for dates)
    LESS_THAN,             // field < value
    GREATER_THAN_EQUAL,    // field >= value
    LESS_THAN_EQUAL        // field <= value
}