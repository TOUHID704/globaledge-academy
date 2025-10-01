package com.globaledge.academy.lms.repository;

import com.globaledge.academy.lms.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {}
