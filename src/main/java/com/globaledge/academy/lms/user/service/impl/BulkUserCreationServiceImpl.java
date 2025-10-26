package com.globaledge.academy.lms.user.service.impl;

import com.globaledge.academy.lms.core.util.PasswordGenerator;
import com.globaledge.academy.lms.core.util.UsernameGenerator;
import com.globaledge.academy.lms.employee.entity.Employee;
import com.globaledge.academy.lms.user.dto.BulkUserCreationSummary;
import com.globaledge.academy.lms.user.entity.User;
import com.globaledge.academy.lms.user.enums.UserRole;
import com.globaledge.academy.lms.user.repository.UserRepository;
import com.globaledge.academy.lms.user.service.BulkUserCreationService;
import com.globaledge.academy.lms.user.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BulkUserCreationServiceImpl implements BulkUserCreationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordGenerator passwordGenerator;
    private final UsernameGenerator usernameGenerator;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public BulkUserCreationSummary createUsersFromEmployees(List<Employee> employees) {
        log.info("Starting bulk user creation for {} employees", employees.size());

        BulkUserCreationSummary summary = BulkUserCreationSummary.builder()
                .totalEmployees(employees.size())
                .build();

        for (Employee employee : employees) {
            try {
                createUserForEmployee(employee, summary);
            } catch (Exception e) {
                log.error("Failed to create user for employee {}: {}",
                        employee.getEmployeeId(), e.getMessage(), e);
                summary.addFailure(employee.getEmployeeId(), employee.getEmail(),
                        "System error: " + e.getMessage());
            }
        }

        log.info("Bulk user creation completed. Created: {}, Skipped: {}, Failed: {}",
                summary.getUsersCreated(), summary.getUsersSkipped(), summary.getUsersFailed());

        return summary;
    }

    private void createUserForEmployee(Employee employee, BulkUserCreationSummary summary) {
        // Check if user already exists by email
        if (userRepository.existsByEmail(employee.getEmail())) {
            log.debug("User already exists with email: {}", employee.getEmail());
            summary.addSkippedEmployee(employee.getEmployeeId());
            return;
        }

        // Check if user already exists by employeeId
        if (userRepository.existsByEmployeeId(employee.getEmployeeId())) {
            log.debug("User already exists for employee ID: {}", employee.getEmployeeId());
            summary.addSkippedEmployee(employee.getEmployeeId());
            return;
        }

        // Generate username
        String baseUsername = usernameGenerator.generateUsername(
                employee.getFirstName(),
                employee.getLastName()
        );

        String username = ensureUniqueUsername(baseUsername);

        // Generate temporary password
        String temporaryPassword = passwordGenerator.generateSecurePassword();

        // Create user
        User user = User.builder()
                .username(username)
                .email(employee.getEmail())
                .password(passwordEncoder.encode(temporaryPassword))
                .employeeId(employee.getEmployeeId())
                .userRole(UserRole.USER)
                .accountEnabled(true)
                .accountLocked(false)
                .passwordExpired(true) // Force password change on first login
                .failedLoginAttempts(0)
                .build();

        userRepository.save(user);
        log.info("Created user '{}' for employee '{}'", username, employee.getEmployeeId());

        // Send welcome email with temporary password (async)
        try {
            notificationService.sendWelcomeEmail(
                    employee.getEmail(),
                    username,
                    temporaryPassword
            );
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}",
                    employee.getEmail(), e.getMessage());
            // Don't fail the user creation if email fails
        }

        summary.addCreatedUsername(username);
    }

    private String ensureUniqueUsername(String baseUsername) {
        if (!userRepository.existsByUsername(baseUsername)) {
            return baseUsername;
        }

        // Try with numeric suffix
        int suffix = 1;
        String username;
        do {
            username = baseUsername + suffix;
            suffix++;
        } while (userRepository.existsByUsername(username) && suffix < 1000);

        if (suffix >= 1000) {
            // If we've tried 1000 variations, use UUID suffix
            return baseUsername + "_" + java.util.UUID.randomUUID().toString().substring(0, 8);
        }

        return username;
    }
}
