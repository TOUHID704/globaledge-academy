// 🎯 enrollment/repository/EnrollmentRepository.java
package com.globaledge.academy.lms.enrollment.repository;

import com.globaledge.academy.lms.enrollment.entity.Enrollment;
import com.globaledge.academy.lms.enrollment.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    // Check if enrollment exists (used to prevent duplicates)
    boolean existsByEmployee_IdAndCourse_CourseId(Long employeeId, Long courseId);

    // Find specific enrollment
    Optional<Enrollment> findByEmployee_EmployeeIdAndCourse_CourseId(String employeeId, Long courseId);

    // Get all enrollments for an employee (by database ID)
    List<Enrollment> findByEmployee_Id(Long employeeId);

    // Get all enrollments for an employee (by employeeId string)
    List<Enrollment> findByEmployee_EmployeeId(String employeeId);

    // Get all enrollments for a course
    List<Enrollment> findByCourse_CourseId(Long courseId);

    // Get enrollments by status for an employee
    List<Enrollment> findByEmployee_IdAndEnrollmentStatus(Long employeeId, EnrollmentStatus status);


    // Count enrollments for a course
    long countByCourse_CourseId(Long courseId);

    // Count enrollments by status for an employee
    long countByEmployee_IdAndEnrollmentStatus(Long employeeId, EnrollmentStatus status);
}