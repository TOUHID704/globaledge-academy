package com.globaledge.academy.lms.employee.imports.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a single row parsed from the Excel file.
 * This is an intermediate DTO that holds raw string values before validation and mapping.
 */
@Getter
@Setter
@Builder
public class EmployeeImportRecord {

    private int rowNumber;

    private String importType;

    private String employeeId;

    private String firstName;

    private String lastName;

    private String email;

    private String department;

    private String designation;

    private String dateOfJoining;

    private String phoneNumber;

    private String dateOfBirth;

    private String gender;

    private String domain;

    private String subDomain;

    private String employmentType;

    private String managerId;

    private String officeLocation;

    private String workMode;

    private String status;
}