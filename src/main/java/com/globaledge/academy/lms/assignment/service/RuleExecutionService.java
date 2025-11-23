// 🎯 assignment/service/RuleExecutionService.java
package com.globaledge.academy.lms.assignment.service;

import com.globaledge.academy.lms.assignment.dto.RuleExecutionResultDto;
import com.globaledge.academy.lms.assignment.entity.CourseAssignmentRule;

public interface RuleExecutionService {
    /**
     * Execute rule by ID
     */
    RuleExecutionResultDto executeRule(Long ruleId);

    /**
     * Execute rule entity
     */
    RuleExecutionResultDto executeRule(CourseAssignmentRule rule);

    /**
     * Execute all scheduled rules (called by scheduler)
     */
    void executeAllScheduledRules();
}