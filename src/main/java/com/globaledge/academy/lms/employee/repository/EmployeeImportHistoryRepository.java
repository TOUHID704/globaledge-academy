package com.globaledge.academy.lms.employee.repository;


import com.globaledge.academy.lms.employee.imports.model.EmployeeImportHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the EmployeeImportHistory entity.
 * Provides CRUD operations and custom finder methods for retrieving import job records.
 */
@Repository
public interface EmployeeImportHistoryRepository extends JpaRepository<EmployeeImportHistory, Long> {

    /**
     * Finds all import history records, ordered by the import timestamp in descending order (most recent first).
     * @return A list of all import history records.
     */
    List<EmployeeImportHistory> findAllByOrderByImportedAtDesc();
}