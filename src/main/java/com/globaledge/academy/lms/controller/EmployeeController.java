package com.globaledge.academy.lms.controller;

import com.globaledge.academy.lms.dto.EmployeeDto;
import com.globaledge.academy.lms.entity.Employee;
import com.globaledge.academy.lms.service.EmployeeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping
    public List<Employee> getAllEmployees() {
        return service.getAllEmployees();
    }

    @PostMapping
    public Employee createEmployee(@RequestBody EmployeeDto employeeDto) {
        return service.createEmployee(employeeDto);
    }
}
