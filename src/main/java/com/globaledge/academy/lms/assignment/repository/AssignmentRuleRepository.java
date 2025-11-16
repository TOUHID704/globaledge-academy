// 🎯 assignment/repository/AssignmentRuleRepository.java
package com.globaledge.academy.lms.assignment.repository;

import com.globaledge.academy.lms.assignment.entity.CourseAssignmentRule;
import com.globaledge.academy.lms.assignment.enums.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRuleRepository extends JpaRepository<CourseAssignmentRule, Long> {

    /**
     * Find all active rules
     */
    List<CourseAssignmentRule> findByIsActiveTrueAndRuleStatus(RuleStatus ruleStatus);

    /**
     * Find rules by course
     */
    List<CourseAssignmentRule> findByCourse_CourseId(Long courseId);

    /**
     * Find active rules by execution frequency
     */
    List<CourseAssignmentRule> findByIsActiveTrueAndRuleStatusAndExecutionFrequency(
            RuleStatus ruleStatus,
            ExecutionFrequency frequency
    );

    /**
     * Find rules that should be executed (active + specific frequency)
     */
    @Query("SELECT r FROM CourseAssignmentRule r " +
            "WHERE r.isActive = true " +
            "AND r.ruleStatus = :status " +
            "AND r.executionFrequency = :frequency " +
            "ORDER BY r.createdAt ASC")
    List<CourseAssignmentRule> findExecutableRules(
            @Param("status") RuleStatus status,
            @Param("frequency") ExecutionFrequency frequency
    );

    /**
     * Count active rules
     */
    long countByIsActiveTrueAndRuleStatus(RuleStatus ruleStatus);

    /**
     * Find rules by created by
     */
    List<CourseAssignmentRule> findByCreatedByOrderByCreatedAtDesc(String createdBy);

    /**
     * Find all rules ordered by creation date
     */
    List<CourseAssignmentRule> findAllByOrderByCreatedAtDesc();

    /**
     * Check if rule name already exists
     */
    boolean existsByRuleNameAndIsActiveTrue(String ruleName);
}