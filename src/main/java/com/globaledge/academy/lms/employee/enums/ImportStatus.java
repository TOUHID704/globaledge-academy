package com.globaledge.academy.lms.employee.enums;

/**
 * Represents the overall status of an employee import job.
 */
public enum ImportStatus {
    /**
     * All records were processed successfully with no errors or warnings.
     */
    SUCCESS,
    /**
     * All valid records were imported, but some generated non-critical warnings.
     */
    COMPLETED_WITH_WARNINGS,
    /**
     * Some records were imported successfully, but others failed due to errors.
     */
    COMPLETED_WITH_ERRORS,
    /**
     * The import process failed completely, and no records were saved.
     */
    FAILED
}