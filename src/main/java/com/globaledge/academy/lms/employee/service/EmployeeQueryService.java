package com.globaledge.academy.lms.employee.service;



import com.globaledge.academy.lms.employee.entity.Employee;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for read-only operations related to the Employee entity.
 * This follows the Command Query Responsibility Segregation (CQRS) principle.
 */
public interface EmployeeQueryService {
    List<Employee> getAllEmployees();
    Optional<Employee> getEmployeeById(Long id);
    Optional<Employee> getEmployeeByEmployeeId(String employeeId);
    Optional<Employee> getEmployeeByEmail(String email);
}