// 🎯 assignment/dto/RuleCriterionDto.java
package com.globaledge.academy.lms.assignment.dto;

import com.globaledge.academy.lms.assignment.enums.FieldOperator;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleCriterionDto {
    private Long criterionId;
    private String fieldName;      // e.g., "department", "designation", "location"
    private FieldOperator operator; // e.g., EQUALS, IN, CONTAINS
    private String fieldValue;      // e.g., "IT" or "Developer,Senior Developer"
}