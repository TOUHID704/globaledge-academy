package com.globaledge.academy.lms.employee.imports.service;


import com.globaledge.academy.lms.employee.imports.dto.EmployeeImportHistoryDTO;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * Interface for retrieving import history and related artifacts like log files.
 */
public interface EmployeeImportHistoryService {

    /**
     * Retrieves all past import job records.
     *
     * @return A list of DTOs representing each import job, sorted by most recent.
     */
    List<EmployeeImportHistoryDTO> getAllImportHistory();

    /**
     * Retrieves a single import job record by its unique ID.
     *
     * @param id The ID of the import history record.
     * @return A DTO with the details of the specified import job.
     */
    EmployeeImportHistoryDTO getImportHistoryById(Long id);

    /**
     * Retrieves the log file generated for a specific import job as a downloadable resource.
     *
     * @param importId The ID of the import job.
     * @return A Spring Resource representing the log file.
     */
    Resource downloadLogFile(Long importId);
}