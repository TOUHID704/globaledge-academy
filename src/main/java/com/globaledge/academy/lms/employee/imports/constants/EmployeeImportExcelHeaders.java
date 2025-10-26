    package com.globaledge.academy.lms.employee.imports.constants;

    /**
     * Defines constant string values for Excel column headers.
     * This prevents typos and centralizes header names.
     */
    public final class EmployeeImportExcelHeaders {
        private EmployeeImportExcelHeaders() {}

        // New mandatory header for the strategy code
        public static final String IMPORT_TYPE = "Import Type";

        // Employee Data Fields
        public static final String EMPLOYEE_ID = "Employee ID";
        public static final String FIRST_NAME = "First Name";
        public static final String LAST_NAME = "Last Name";
        public static final String EMAIL = "Email";
        public static final String DEPARTMENT = "Department";
        public static final String DESIGNATION = "Designation";
        public static final String DATE_OF_JOINING = "Date of Joining";
        public static final String PHONE_NUMBER = "Phone Number";
        public static final String DATE_OF_BIRTH = "Date of Birth";
        public static final String GENDER = "Gender";
        public static final String DOMAIN = "Domain";
        public static final String SUB_DOMAIN = "Sub Domain";
        public static final String EMPLOYMENT_TYPE = "Employment Type";
        public static final String MANAGER_ID = "Manager ID";
        public static final String OFFICE_LOCATION = "Office Location";
        public static final String WORK_MODE = "Work Mode";
        public static final String STATUS = "Status";

        // Log File Headers
        public static final String LOG_LEVEL = "LEVEL";
        public static final String LOG_IDENTIFIER = "IDENTIFIER";
        public static final String LOG_MESSAGE = "MESSAGE";
    }