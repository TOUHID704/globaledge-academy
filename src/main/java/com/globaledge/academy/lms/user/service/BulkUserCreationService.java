package com.globaledge.academy.lms.user.service;

import com.globaledge.academy.lms.employee.entity.Employee;
import com.globaledge.academy.lms.user.dto.BulkUserCreationSummary;

import java.util.List;

public interface BulkUserCreationService {
    BulkUserCreationSummary createUsersFromEmployees(List<Employee> employees);
}