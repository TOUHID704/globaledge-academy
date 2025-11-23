// 🎯 assignment/dto/RulePreviewDto.java
package com.globaledge.academy.lms.assignment.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RulePreviewDto {
    private int totalMatched;
    private int alreadyEnrolled;
    private int willBeEnrolled;
    private List<MatchedEmployeeDto> matchedEmployees;
    private String generatedQuery;  // For debugging
}