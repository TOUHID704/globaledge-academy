// 🎯 assignment/enums/RuleType.java
package com.globaledge.academy.lms.assignment.enums;

public enum RuleType {
    DEPARTMENT_BASED,      // Based on employee department
    DESIGNATION_BASED,     // Based on job title/designation
    LOCATION_BASED,        // Based on office location
    WORK_MODE_BASED,       // Based on work mode (Remote/Hybrid/Office)
    EMPLOYMENT_TYPE_BASED, // Based on employment type (Full-Time/Contractor)
    CUSTOM                 // Multiple criteria combination
}