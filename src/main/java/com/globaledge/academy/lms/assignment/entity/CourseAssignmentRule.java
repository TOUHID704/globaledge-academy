// 🎯 assignment/entity/CourseAssignmentRule.java
package com.globaledge.academy.lms.assignment.entity;

import com.globaledge.academy.lms.assignment.enums.*;
import com.globaledge.academy.lms.course.entity.Course;
import com.globaledge.academy.lms.enrollment.enums.EnrollmentType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "course_assignment_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseAssignmentRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ruleId;

    @Column(nullable = false)
    private String ruleName;

    @Column(length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuleType ruleType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RuleStatus ruleStatus = RuleStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentType enrollmentType;  // MANDATORY or OPTIONAL

    private Integer dueDays;  // Days from assignment to due date

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExecutionFrequency executionFrequency;

    @Column(nullable = false)
    @Builder.Default
    private String matchLogic = "AND";  // "AND" or "OR"

    private LocalDateTime lastExecutedAt;

    private Integer lastMatchedCount;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private String createdBy;

    @Column(nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @OneToMany(mappedBy = "assignmentRule", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RuleCriterion> criteria = new ArrayList<>();

    // Helper methods
    public void addCriterion(RuleCriterion criterion) {
        criteria.add(criterion);
        criterion.setAssignmentRule(this);
    }

    public void removeCriterion(RuleCriterion criterion) {
        criteria.remove(criterion);
        criterion.setAssignmentRule(null);
    }
}