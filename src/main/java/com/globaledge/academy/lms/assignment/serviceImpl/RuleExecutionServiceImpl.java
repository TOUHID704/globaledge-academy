// 🎯 assignment/service/impl/RuleExecutionServiceImpl.java
package com.globaledge.academy.lms.assignment.serviceImpl;

import com.globaledge.academy.lms.assignment.dto.RuleExecutionResultDto;
import com.globaledge.academy.lms.assignment.entity.CourseAssignmentRule;
import com.globaledge.academy.lms.assignment.enums.*;
import com.globaledge.academy.lms.assignment.exception.*;
import com.globaledge.academy.lms.assignment.repository.AssignmentRuleRepository;
import com.globaledge.academy.lms.assignment.service.*;
import com.globaledge.academy.lms.employee.entity.Employee;
import com.globaledge.academy.lms.enrollment.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RuleExecutionServiceImpl implements RuleExecutionService {

    private final AssignmentRuleRepository assignmentRuleRepository;
    private final RuleEvaluationService ruleEvaluationService;
    private final EnrollmentService enrollmentService;

    @Override
    @Transactional
    public RuleExecutionResultDto executeRule(Long ruleId) {
        log.info("Executing assignment rule by ID: {}", ruleId);

        CourseAssignmentRule rule = assignmentRuleRepository.findById(ruleId)
                .orElseThrow(() -> new AssignmentRuleNotFoundException("Rule not found with ID: " + ruleId));

        return executeRule(rule);
    }

    @Override
    @Transactional
    public RuleExecutionResultDto executeRule(CourseAssignmentRule rule) {
        long startTime = System.currentTimeMillis();

        try {
            log.info("Executing rule: '{}' for course: '{}'", rule.getRuleName(), rule.getCourse().getTitle());

            // Validate rule is active
            if (!rule.isActive() || rule.getRuleStatus() != RuleStatus.ACTIVE) {
                log.warn("Rule {} is not active. Skipping execution.", rule.getRuleId());
                return RuleExecutionResultDto.builder()
                        .ruleId(rule.getRuleId())
                        .ruleName(rule.getRuleName())
                        .success(false)
                        .totalMatched(0)
                        .enrollmentsCreated(0)
                        .enrollmentsSkipped(0)
                        .executedAt(LocalDateTime.now())
                        .executionTimeMs(System.currentTimeMillis() - startTime)
                        .message("Rule is not active")
                        .build();
            }

            // Find matching employees
            List<Employee> matchedEmployees = ruleEvaluationService.findMatchingEmployees(rule);

            int enrollmentsCreated = 0;
            int enrollmentsSkipped = 0;
            List<String> errors = new ArrayList<>();

            // Create enrollments
            for (Employee employee : matchedEmployees) {
                try {
                    boolean created = enrollmentService.createEnrollmentFromRule(
                            employee.getId(),
                            rule.getCourse().getCourseId(),
                            rule.getEnrollmentType(),
                            rule.getDueDays()
                    );

                    if (created) {
                        enrollmentsCreated++;
                        log.debug("Created enrollment for employee: {}", employee.getEmployeeId());
                    } else {
                        enrollmentsSkipped++;
                        log.debug("Skipped enrollment for employee: {} (already enrolled)", employee.getEmployeeId());
                    }
                } catch (Exception e) {
                    log.error("Error creating enrollment for employee {}: {}",
                            employee.getEmployeeId(), e.getMessage());
                    errors.add("Employee " + employee.getEmployeeId() + ": " + e.getMessage());
                }
            }

            // Update rule execution info
            rule.setLastExecutedAt(LocalDateTime.now());
            rule.setLastMatchedCount(matchedEmployees.size());
            assignmentRuleRepository.save(rule);

            long executionTime = System.currentTimeMillis() - startTime;

            log.info("Rule execution completed: {} enrollments created, {} skipped, {} errors",
                    enrollmentsCreated, enrollmentsSkipped, errors.size());

            return RuleExecutionResultDto.builder()
                    .ruleId(rule.getRuleId())
                    .ruleName(rule.getRuleName())
                    .success(true)
                    .totalMatched(matchedEmployees.size())
                    .enrollmentsCreated(enrollmentsCreated)
                    .enrollmentsSkipped(enrollmentsSkipped)
                    .errors(errors)
                    .executedAt(LocalDateTime.now())
                    .executionTimeMs(executionTime)
                    .message(String.format("Successfully executed rule. Matched: %d, Created: %d, Skipped: %d",
                            matchedEmployees.size(), enrollmentsCreated, enrollmentsSkipped))
                    .build();

        } catch (Exception e) {
            log.error("Error executing rule {}: {}", rule.getRuleId(), e.getMessage(), e);
            throw new RuleExecutionException("Failed to execute rule: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void executeAllScheduledRules() {
        log.info("Executing all scheduled assignment rules");

        // Get all active daily rules
        List<CourseAssignmentRule> dailyRules = assignmentRuleRepository
                .findExecutableRules(RuleStatus.ACTIVE, ExecutionFrequency.DAILY);

        log.info("Found {} daily rules to execute", dailyRules.size());

        int successCount = 0;
        int failureCount = 0;

        for (CourseAssignmentRule rule : dailyRules) {
            try {
                RuleExecutionResultDto result = executeRule(rule);
                if (result.isSuccess()) {
                    successCount++;
                } else {
                    failureCount++;
                }
            } catch (Exception e) {
                log.error("Error executing rule {}: {}", rule.getRuleId(), e.getMessage());
                failureCount++;
            }
        }

        log.info("Completed executing {} scheduled rules. Success: {}, Failures: {}",
                dailyRules.size(), successCount, failureCount);
    }
}