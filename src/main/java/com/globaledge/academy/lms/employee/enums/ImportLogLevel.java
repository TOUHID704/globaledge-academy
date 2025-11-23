package com.globaledge.academy.lms.employee.enums;

/**
 * Defines the severity level for an entry in the import log file.
 */
public enum ImportLogLevel {
    /**
     * A critical issue that prevented a record from being processed.
     */
    ERROR,
    /**
     * A non-critical issue that did not prevent processing but should be noted.
     */
    WARNING,
    /**
     * General information about the import process.
     */
    INFO
}