package com.globaledge.academy.lms.service;

import com.globaledge.academy.lms.dto.EmployeeDto;
import com.globaledge.academy.lms.entity.Employee;

import java.util.List;

public interface EmployeeService {
    List<Employee> getAllEmployees();
    Employee createEmployee(EmployeeDto employeeDto);
}
