// 🎯 assignment/dto/RuleExecutionResultDto.java
package com.globaledge.academy.lms.assignment.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleExecutionResultDto {
    private Long ruleId;
    private String ruleName;
    private boolean success;
    private int totalMatched;
    private int enrollmentsCreated;
    private int enrollmentsSkipped;  // Already enrolled
    @Builder.Default
    private List<String> errors = new ArrayList<>();
    private LocalDateTime executedAt;
    private long executionTimeMs;
    private String message;
}