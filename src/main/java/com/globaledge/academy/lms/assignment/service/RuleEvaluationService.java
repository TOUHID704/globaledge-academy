// 🎯 assignment/service/RuleEvaluationService.java
package com.globaledge.academy.lms.assignment.service;

import com.globaledge.academy.lms.assignment.dto.RulePreviewDto;
import com.globaledge.academy.lms.assignment.entity.CourseAssignmentRule;
import com.globaledge.academy.lms.employee.entity.Employee;

import java.util.List;

public interface RuleEvaluationService {
    /**
     * Find employees matching the rule criteria
     */
    List<Employee> findMatchingEmployees(CourseAssignmentRule rule);

    /**
     * Preview rule execution (shows who will be enrolled without creating enrollments)
     */
    RulePreviewDto previewRule(CourseAssignmentRule rule, Long courseId);
}