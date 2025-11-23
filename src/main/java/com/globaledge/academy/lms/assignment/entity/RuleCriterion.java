// 🎯 assignment/entity/RuleCriterion.java
package com.globaledge.academy.lms.assignment.entity;

import com.globaledge.academy.lms.assignment.enums.FieldOperator;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rule_criteria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleCriterion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long criterionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false)
    private CourseAssignmentRule assignmentRule;

    @Column(nullable = false)
    private String fieldName;  // e.g., "department", "designation", "location"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FieldOperator operator;

    @Column(nullable = false, length = 1000)
    private String fieldValue;  // e.g., "IT" or "Developer,Senior Developer"

    @Column(nullable = false)
    @Builder.Default
    private Integer criterionOrder = 0;  // Order of evaluation
}