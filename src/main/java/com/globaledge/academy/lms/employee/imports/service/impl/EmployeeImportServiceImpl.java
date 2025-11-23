package com.globaledge.academy.lms.employee.imports.service.impl;

import com.globaledge.academy.lms.employee.entity.Employee;
import com.globaledge.academy.lms.employee.imports.model.EmployeeImportHistory;
import com.globaledge.academy.lms.employee.enums.ImportStatus;
import com.globaledge.academy.lms.employee.enums.ImportStrategyType;
import com.globaledge.academy.lms.employee.exception.EmployeeImportProcessingException;
import com.globaledge.academy.lms.employee.imports.dto.EmployeeImportLogEntryDTO;
import com.globaledge.academy.lms.employee.imports.dto.EmployeeImportResultDTO;
import com.globaledge.academy.lms.employee.imports.mapper.EmployeeImportMapper;
import com.globaledge.academy.lms.employee.imports.model.EmployeeImportContext;
import com.globaledge.academy.lms.employee.imports.model.EmployeeImportRecord;
import com.globaledge.academy.lms.employee.imports.model.EmployeeImportValidationResult;
import com.globaledge.academy.lms.employee.imports.parser.ExcelParser;
import com.globaledge.academy.lms.employee.imports.service.EmployeeImportService;
import com.globaledge.academy.lms.employee.imports.strategy.EmployeeImportStrategy;
import com.globaledge.academy.lms.employee.imports.validator.chain.EmployeeImportValidator;
import com.globaledge.academy.lms.employee.imports.writer.ImportLogWriter;
import com.globaledge.academy.lms.employee.repository.EmployeeImportHistoryRepository;
import com.globaledge.academy.lms.user.dto.BulkUserCreationSummary;
import com.globaledge.academy.lms.user.service.BulkUserCreationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeImportServiceImpl implements EmployeeImportService {

    private final ExcelParser excelParser;
    private final EmployeeImportValidator validator;
    private final EmployeeImportMapper mapper;
    private final ImportLogWriter logWriter;
    private final EmployeeImportHistoryRepository historyRepository;
    private final Map<String, EmployeeImportStrategy> importStrategyMap;
    private final BulkUserCreationService bulkUserCreationService;

    @Override
    @Transactional
    public EmployeeImportResultDTO importEmployees(MultipartFile file, String importedBy) {
        try {
            log.info("Starting employee import. File: {}, ImportedBy: {}", file.getOriginalFilename(), importedBy);

            List<EmployeeImportRecord> records = excelParser.parseFile(file);

            Set<String> employeeIdsInBatch = records.stream()
                    .map(EmployeeImportRecord::getEmployeeId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            List<EmployeeImportValidationResult> validationResults = validator.validateRecords(records, employeeIdsInBatch);

            // PASS 1: Create/update all employees and collect them in a map
            Map<String, Employee> processedEmployeesMap = processRecordsPassOne(validationResults, importedBy);
            int successCount = processedEmployeesMap.size();

            // PASS 2: Link managers now that all employees are in the persistence context
            linkManagersPassTwo(validationResults, processedEmployeesMap);

            // PASS 3: Create user accounts for successfully imported employees
            BulkUserCreationSummary userCreationSummary = null;
            if (!processedEmployeesMap.isEmpty()) {
                List<Employee> successfulEmployees = List.copyOf(processedEmployeesMap.values());
                userCreationSummary = bulkUserCreationService.createUsersFromEmployees(successfulEmployees);
                log.info("User creation summary - Created: {}, Skipped: {}, Failed: {}",
                        userCreationSummary.getUsersCreated(),
                        userCreationSummary.getUsersSkipped(),
                        userCreationSummary.getUsersFailed());
            }

            List<EmployeeImportLogEntryDTO> allLogs = collectAllLogs(validationResults);
            String logFilePath = null, logFileName = null;
            if (!allLogs.isEmpty()) {
                logFileName = generateLogFileName(file.getOriginalFilename());
                logFilePath = logWriter.writeLogFile(allLogs, logFileName);
            }

            int errorCount = (int) validationResults.stream().filter(EmployeeImportValidationResult::hasErrors).count();
            int warningCount = allLogs.size() - errorCount;
            ImportStatus status = determineImportStatus(successCount, errorCount, records.size());

            EmployeeImportHistory history = saveImportHistory(
                    createImportContext(file, importedBy),
                    records.size(),
                    successCount,
                    errorCount,
                    warningCount,
                    status,
                    logFilePath,
                    logFileName
            );

            log.info("Employee import completed. Status: {}, Success: {}, Errors: {}, Warnings: {}",
                    status, successCount, errorCount, warningCount);

            return buildImportResult(history, userCreationSummary);

        } catch (IOException e) {
            throw new EmployeeImportProcessingException("Failed to process the uploaded file.", e);
        } catch (Exception e) {
            throw new EmployeeImportProcessingException("An unexpected error occurred during import.", e);
        }
    }

    private Map<String, Employee> processRecordsPassOne(List<EmployeeImportValidationResult> validationResults, String importedBy) {
        log.info("Starting pass one: Processing {} validation results.", validationResults.size());
        return validationResults.stream()
                .filter(result -> !result.hasErrors())
                .map(result -> {
                    try {
                        ImportStrategyType strategyType = ImportStrategyType.fromCode(result.getRecord().getImportType());
                        EmployeeImportStrategy strategy = importStrategyMap.get(strategyType.getBeanName());
                        Employee employee = mapper.toEntity(result.getRecord(), importedBy);
                        return strategy.process(employee, result.getRecord());
                    } catch (Exception e) {
                        log.error("System error during pass one for row {}: {}", result.getRecord().getRowNumber(), e.getMessage(), e);
                        result.addError(validator.createLog(result.getRecord(), "System error: " + e.getMessage()));
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Employee::getEmployeeId, Function.identity()));
    }

    private void linkManagersPassTwo(List<EmployeeImportValidationResult> validationResults, Map<String, Employee> processedEmployeesMap) {
        log.info("Starting pass two: Linking managers for {} processed employees.", processedEmployeesMap.size());
        for (EmployeeImportValidationResult result : validationResults) {
            if (result.hasErrors()) continue;

            String employeeId = result.getRecord().getEmployeeId();
            String managerId = result.getRecord().getManagerId();

            if (managerId != null && !managerId.trim().isEmpty()) {
                Employee employee = processedEmployeesMap.get(employeeId);
                Employee manager = processedEmployeesMap.get(managerId);

                if (employee != null && manager != null) {
                    employee.setManager(manager);
                }
            }
        }
    }

    private EmployeeImportContext createImportContext(MultipartFile file, String importedBy) {
        return EmployeeImportContext.builder()
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .importedBy(importedBy)
                .build();
    }

    private List<EmployeeImportLogEntryDTO> collectAllLogs(List<EmployeeImportValidationResult> results) {
        return results.stream()
                .flatMap(r -> Stream.concat(r.getErrors().stream(), r.getWarnings().stream()))
                .collect(Collectors.toList());
    }

    private String generateLogFileName(String originalFileName) {
        String name = originalFileName.replaceFirst("[.][^.]+$", "");
        return String.format("%s_log_%s.xlsx", name, LocalDateTime.now().toString().replaceAll("[:.]", "-"));
    }

    private ImportStatus determineImportStatus(int successCount, int errorCount, int totalRecords) {
        if (errorCount == totalRecords && totalRecords > 0) return ImportStatus.FAILED;
        if (errorCount > 0) return ImportStatus.COMPLETED_WITH_ERRORS;
        if (successCount + errorCount < totalRecords) return ImportStatus.COMPLETED_WITH_WARNINGS;
        return ImportStatus.SUCCESS;
    }

    private EmployeeImportHistory saveImportHistory(
            EmployeeImportContext ctx,
            int total,
            int success,
            int error,
            int warn,
            ImportStatus status,
            String logPath,
            String logName) {

        return historyRepository.save(EmployeeImportHistory.builder()
                .fileName(ctx.getFileName())
                .fileSize(ctx.getFileSize())
                .totalRecords(total)
                .successCount(success)
                .errorCount(error)
                .warningCount(warn)
                .status(status)
                .logFilePath(logPath)
                .logFileName(logName)
                .importedBy(ctx.getImportedBy())
                .build());
    }

    private EmployeeImportResultDTO buildImportResult(EmployeeImportHistory h, BulkUserCreationSummary userSummary) {
        return EmployeeImportResultDTO.builder()
                .importId(h.getId())
                .fileName(h.getFileName())
                .fileSize(h.getFileSize())
                .totalRecords(h.getTotalRecords())
                .successCount(h.getSuccessCount())
                .errorCount(h.getErrorCount())
                .warningCount(h.getWarningCount())
                .status(h.getStatus())
                .logFileName(h.getLogFileName())
                .importedAt(h.getImportedAt())
                .importedBy(h.getImportedBy())
                .message(buildResultMessage(h))
                .userCreationSummary(userSummary)
                .build();
    }

    private String buildResultMessage(EmployeeImportHistory h) {
        switch (h.getStatus()) {
            case SUCCESS:
                return String.format("All %d records imported successfully.", h.getSuccessCount());
            case COMPLETED_WITH_WARNINGS:
                return String.format("%d records imported successfully with %d warnings. See log file for details.",
                        h.getSuccessCount(), h.getWarningCount());
            case COMPLETED_WITH_ERRORS:
                return String.format("%d records imported successfully, while %d failed. See log file for details.",
                        h.getSuccessCount(), h.getErrorCount());
            case FAILED:
                return "Import failed. All records had critical errors. See log file for details.";
            default:
                return "Import process finished.";
        }
    }
}

