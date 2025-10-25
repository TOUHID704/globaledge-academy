package com.globaledge.academy.lms.employee.repository;


import com.globaledge.academy.lms.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the Employee entity.
 * Provides CRUD (Create, Read, Update, Delete) operations and custom finder methods.
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Finds an employee by their unique employee ID.
     * @param employeeId The employee ID to search for.
     * @return An Optional containing the found employee, or an empty Optional if not found.
     */
    Optional<Employee> findByEmployeeId(String employeeId);

    /**
     * Finds an employee by their unique email address.
     * @param email The email address to search for.
     * @return An Optional containing the found employee, or an empty Optional if not found.
     */
    Optional<Employee> findByEmail(String email);

    /**
     * Checks if an employee exists with the given employee ID.
     * @param employeeId The employee ID to check.
     * @return true if an employee exists, false otherwise.
     */
    boolean existsByEmployeeId(String employeeId);

    /**
     * Checks if an employee exists with the given email address.
     * @param email The email address to check.
     * @return true if an employee exists, false otherwise.
     */
    boolean existsByEmail(String email);
}