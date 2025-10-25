package com.globaledge.academy.lms.employee.service.impl;


import com.globaledge.academy.lms.employee.entity.Employee;
import com.globaledge.academy.lms.employee.repository.EmployeeRepository;
import com.globaledge.academy.lms.employee.service.EmployeeQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeQueryServiceImpl implements EmployeeQueryService {

    private final EmployeeRepository employeeRepository;

    @Override
    public List<Employee> getAllEmployees() {
        log.info("Fetching all employees");
        return employeeRepository.findAll();
    }

    @Override
    public Optional<Employee> getEmployeeById(Long id) {
        log.info("Fetching employee by ID: {}", id);
        return employeeRepository.findById(id);
    }

    @Override
    public Optional<Employee> getEmployeeByEmployeeId(String employeeId) {
        log.info("Fetching employee by employee ID: {}", employeeId);
        return employeeRepository.findByEmployeeId(employeeId);
    }

    @Override
    public Optional<Employee> getEmployeeByEmail(String email) {
        log.info("Fetching employee by email: {}", email);
        return employeeRepository.findByEmail(email);
    }
}