// 🎯 enrollment/service/impl/EnrollmentServiceImpl.java
package com.globaledge.academy.lms.enrollment.service.impl;

import com.globaledge.academy.lms.course.entity.Course;
import com.globaledge.academy.lms.course.enums.CourseStatus;
import com.globaledge.academy.lms.course.repository.CourseRepository;
import com.globaledge.academy.lms.employee.entity.Employee;
import com.globaledge.academy.lms.employee.exception.ResourceNotFoundException;
import com.globaledge.academy.lms.employee.repository.EmployeeRepository;
import com.globaledge.academy.lms.enrollment.dto.EnrollmentDto;
import com.globaledge.academy.lms.enrollment.dto.MyCoursesDto;
import com.globaledge.academy.lms.enrollment.entity.Enrollment;
import com.globaledge.academy.lms.enrollment.enums.*;
import com.globaledge.academy.lms.enrollment.repository.EnrollmentRepository;
import com.globaledge.academy.lms.enrollment.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final EmployeeRepository employeeRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional
    public boolean createEnrollmentFromRule(Long employeeId, Long courseId,
                                            EnrollmentType enrollmentType, Integer dueDays) {

        log.debug("Attempting to create enrollment for employee {} in course {}", employeeId, courseId);

        // Check if already enrolled (prevent duplicates)
        if (enrollmentRepository.existsByEmployee_IdAndCourse_CourseId(employeeId, courseId)) {
            log.debug("Employee {} already enrolled in course {}", employeeId, courseId);
            return false; // Not created, already exists
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));

        LocalDate dueDate = dueDays != null ? LocalDate.now().plusDays(dueDays) : null;

        Enrollment enrollment = Enrollment.builder()
                .employee(employee)
                .course(course)
                .enrollmentType(enrollmentType)
                .enrollmentStatus(EnrollmentStatus.NOT_STARTED)
                .assignmentType(AssignmentType.RULE_BASED)
                .enrolledDate(LocalDate.now())
                .dueDate(dueDate)
                .progressPercentage(0)
                .build();

        enrollmentRepository.save(enrollment);
        log.info("Created rule-based enrollment for employee {} in course {}", employeeId, courseId);

        return true; // Successfully created
    }

    @Override
    @Transactional
    public EnrollmentDto selfEnroll(String employeeId, Long courseId) {
        log.info("Self-enrollment request: employee={}, course={}", employeeId, courseId);

        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + employeeId));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));

        // Check if course is published
        if (course.getCourseStatus() != CourseStatus.PUBLISHED) {
            throw new IllegalStateException("Cannot enroll in unpublished course");
        }

        // Check if already enrolled
        if (enrollmentRepository.existsByEmployee_IdAndCourse_CourseId(employee.getId(), courseId)) {
            throw new IllegalStateException("Already enrolled in this course");
        }

        Enrollment enrollment = Enrollment.builder()
                .employee(employee)
                .course(course)
                .enrollmentType(EnrollmentType.OPTIONAL)
                .enrollmentStatus(EnrollmentStatus.NOT_STARTED)
                .assignmentType(AssignmentType.SELF_ENROLLED)
                .enrolledDate(LocalDate.now())
                .progressPercentage(0)
                .build();

        Enrollment saved = enrollmentRepository.save(enrollment);
        log.info("Self-enrollment successful: employee={}, course={}", employeeId, courseId);

        return convertToDto(saved);
    }

    @Override
    public MyCoursesDto getMyEnrolledCourses(String employeeId) {
        log.debug("Fetching enrolled courses for employee: {}", employeeId);

        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + employeeId));

        List<Enrollment> enrollments = enrollmentRepository.findByEmployee_Id(employee.getId());

        List<EnrollmentDto> enrolledCourses = enrollments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        // Calculate statistics
        long completed = enrollments.stream()
                .filter(e -> e.getEnrollmentStatus() == EnrollmentStatus.COMPLETED)
                .count();

        long inProgress = enrollments.stream()
                .filter(e -> e.getEnrollmentStatus() == EnrollmentStatus.IN_PROGRESS)
                .count();

        long notStarted = enrollments.stream()
                .filter(e -> e.getEnrollmentStatus() == EnrollmentStatus.NOT_STARTED)
                .count();

        return MyCoursesDto.builder()
                .enrolledCourses(enrolledCourses)
                .totalEnrolled(enrolledCourses.size())
                .completedCount((int) completed)
                .inProgressCount((int) inProgress)
                .notStartedCount((int) notStarted)
                .build();
    }

    @Override
    public List<EnrollmentDto> getAvailableCourses(String employeeId) {
        log.debug("Fetching available courses for employee: {}", employeeId);

        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + employeeId));

        // Get all published courses
        List<Course> allPublished = courseRepository.findByCourseStatus(CourseStatus.PUBLISHED);

        // Get already enrolled course IDs
        List<Long> enrolledCourseIds = enrollmentRepository
                .findByEmployee_Id(employee.getId())
                .stream()
                .map(e -> e.getCourse().getCourseId())
                .collect(Collectors.toList());

        // Filter out enrolled courses
        return allPublished.stream()
                .filter(c -> !enrolledCourseIds.contains(c.getCourseId()))
                .map(this::convertCourseToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EnrollmentDto updateProgress(Long enrollmentId, Integer progressPercentage) {
        log.debug("Updating progress: enrollment={}, progress={}", enrollmentId, progressPercentage);

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found: " + enrollmentId));

        enrollment.setProgressPercentage(progressPercentage);

        // Update status based on progress
        if (progressPercentage >= 100) {
            enrollment.setEnrollmentStatus(EnrollmentStatus.COMPLETED);
            enrollment.setCompletedDate(LocalDate.now());
            log.info("Enrollment {} marked as completed", enrollmentId);
        } else if (progressPercentage > 0) {
            enrollment.setEnrollmentStatus(EnrollmentStatus.IN_PROGRESS);
        }

        Enrollment updated = enrollmentRepository.save(enrollment);
        return convertToDto(updated);
    }

    @Override
    @Transactional
    public EnrollmentDto markAsCompleted(Long enrollmentId) {
        log.info("Marking enrollment as completed: {}", enrollmentId);

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found: " + enrollmentId));

        enrollment.setEnrollmentStatus(EnrollmentStatus.COMPLETED);
        enrollment.setProgressPercentage(100);
        enrollment.setCompletedDate(LocalDate.now());

        Enrollment updated = enrollmentRepository.save(enrollment);
        return convertToDto(updated);
    }

    // Helper methods
    private EnrollmentDto convertToDto(Enrollment enrollment) {
        Course course = enrollment.getCourse();

        return EnrollmentDto.builder()
                .enrollmentId(enrollment.getEnrollmentId())
                .courseId(course.getCourseId())
                .courseTitle(course.getTitle())
                .courseDescription(course.getDescription())
                .courseThumbnail(course.getThumbnailUrl())
                .courseInstructor(course.getInstructor())
                .courseEstimatedDuration(course.getEstimatedDuration())
                .enrollmentType(enrollment.getEnrollmentType())
                .enrollmentStatus(enrollment.getEnrollmentStatus())
                .assignmentType(enrollment.getAssignmentType())
                .enrolledDate(enrollment.getEnrolledDate())
                .dueDate(enrollment.getDueDate())
                .completedDate(enrollment.getCompletedDate())
                .progressPercentage(enrollment.getProgressPercentage())
                .build();
    }

    private EnrollmentDto convertCourseToDto(Course course) {
        return EnrollmentDto.builder()
                .courseId(course.getCourseId())
                .courseTitle(course.getTitle())
                .courseDescription(course.getDescription())
                .courseThumbnail(course.getThumbnailUrl())
                .courseInstructor(course.getInstructor())
                .courseEstimatedDuration(course.getEstimatedDuration())
                .build();
    }
}