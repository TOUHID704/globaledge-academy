package com.globaledge.academy.lms.employee.imports.strategy.impl;


import com.globaledge.academy.lms.employee.entity.Employee;
import com.globaledge.academy.lms.employee.imports.model.EmployeeImportRecord;
import com.globaledge.academy.lms.employee.imports.strategy.EmployeeImportStrategy;
import com.globaledge.academy.lms.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("updateExistingImportStrategy")
@RequiredArgsConstructor
public class UpdateExistingImportStrategy implements EmployeeImportStrategy {

    private final EmployeeRepository employeeRepository;

    @Override
    public Employee process(Employee employee, EmployeeImportRecord record) {
        return employeeRepository.findByEmployeeId(employee.getEmployeeId())
                .map(existingEmployee -> {
                    log.debug("Updating existing employee from row {}: {}", record.getRowNumber(), existingEmployee.getEmployeeId());
                    updateEmployeeFields(existingEmployee, employee);
                    return employeeRepository.save(existingEmployee);
                })
                .orElseGet(() -> {
                    log.warn("Skipping record for row {}: Employee with ID '{}' not found for update.", record.getRowNumber(), record.getEmployeeId());
                    return null;
                });
    }

    private void updateEmployeeFields(Employee existing, Employee updated) {
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setEmail(updated.getEmail());
        existing.setDepartment(updated.getDepartment());
        existing.setDesignation(updated.getDesignation());
        existing.setDateOfJoining(updated.getDateOfJoining());
        existing.setPhoneNumber(updated.getPhoneNumber());
        existing.setDateOfBirth(updated.getDateOfBirth());
        existing.setGender(updated.getGender());
        existing.setDomain(updated.getDomain());
        existing.setSubDomain(updated.getSubDomain());
        existing.setEmploymentType(updated.getEmploymentType());
        // The manager is NOT set here. It's handled in the service's second pass.
        existing.setOfficeLocation(updated.getOfficeLocation());
        existing.setWorkMode(updated.getWorkMode());
        existing.setStatus(updated.getStatus());
    }
}