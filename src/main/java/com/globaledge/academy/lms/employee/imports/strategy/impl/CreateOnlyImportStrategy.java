package com.globaledge.academy.lms.employee.imports.strategy.impl;


import com.globaledge.academy.lms.employee.entity.Employee;
import com.globaledge.academy.lms.employee.imports.model.EmployeeImportRecord;
import com.globaledge.academy.lms.employee.imports.strategy.EmployeeImportStrategy;
import com.globaledge.academy.lms.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Strategy implementation for creating new employees only (Code 101).
 * It will skip any record where the employee ID or email already exists in the database.
 */
@Slf4j
@Component("createOnlyImportStrategy")
@RequiredArgsConstructor
public class CreateOnlyImportStrategy implements EmployeeImportStrategy {

    private final EmployeeRepository employeeRepository;

    @Override
    public Employee process(Employee employee, EmployeeImportRecord record) {
        // Check for existence by either unique identifier
        if (employeeRepository.existsByEmployeeId(employee.getEmployeeId()) || employeeRepository.existsByEmail(employee.getEmail())) {
            log.warn("Skipping record for row {}: Employee with ID '{}' or email '{}' already exists.", record.getRowNumber(), record.getEmployeeId(), record.getEmail());
            // This record is intentionally skipped, so we return null.
            return null;
        }
        log.debug("Creating new employee from row {}: {}", record.getRowNumber(), employee.getEmployeeId());
        return employeeRepository.save(employee);
    }
}