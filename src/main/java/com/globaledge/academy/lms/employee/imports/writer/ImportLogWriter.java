package com.globaledge.academy.lms.employee.imports.writer;



import com.globaledge.academy.lms.employee.imports.dto.EmployeeImportLogEntryDTO;

import java.io.IOException;
import java.util.List;

/**
 * Interface for a component that writes import log entries to a file.
 */
public interface ImportLogWriter {

    /**
     * Writes a list of log entries to a specified file.
     *
     * @param logEntries The list of log entries (errors and warnings) to write.
     * @param fileName The name of the file to create.
     * @return The absolute path to the newly created log file.
     * @throws IOException if an error occurs during file writing.
     */
    String writeLogFile(List<EmployeeImportLogEntryDTO> logEntries, String fileName) throws IOException;
}