// 🎯 assignment/dto/AssignmentRuleDto.java
package com.globaledge.academy.lms.assignment.dto;

import com.globaledge.academy.lms.assignment.enums.*;
import com.globaledge.academy.lms.enrollment.enums.EnrollmentType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentRuleDto {
    private Long ruleId;
    private String ruleName;
    private String description;
    private Long courseId;
    private String courseTitle;  // For display
    private RuleType ruleType;
    private RuleStatus ruleStatus;
    private EnrollmentType enrollmentType;  // MANDATORY or OPTIONAL
    private Integer dueDays;  // Days from assignment to due date
    private ExecutionFrequency executionFrequency;
    private LocalDateTime lastExecutedAt;
    private Integer lastMatchedCount;
    private String matchLogic;  // "AND" or "OR"
    private List<RuleCriterionDto> criteria;
    private LocalDateTime createdAt;
    private String createdBy;
    private boolean isActive;
}