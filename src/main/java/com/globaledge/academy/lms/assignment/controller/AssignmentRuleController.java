// 🎯 assignment/controller/AssignmentRuleController.java
package com.globaledge.academy.lms.assignment.controller;

import com.globaledge.academy.lms.assignment.dto.*;
import com.globaledge.academy.lms.assignment.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assignment/rules")
@RequiredArgsConstructor
@Tag(name = "Assignment Rules", description = "Course assignment automation APIs")
public class AssignmentRuleController {

    private final AssignmentRuleService assignmentRuleService;
    private final RuleExecutionService ruleExecutionService;

    @Operation(summary = "Create Assignment Rule",
            description = "Create a new rule to automatically assign courses to employees")
    @PostMapping
    public ResponseEntity<AssignmentRuleDto> createRule(@RequestBody AssignmentRuleDto ruleDto) {
        return ResponseEntity.ok(assignmentRuleService.createRule(ruleDto));
    }

    @Operation(summary = "Get All Rules")
    @GetMapping
    public ResponseEntity<List<AssignmentRuleDto>> getAllRules() {
        return ResponseEntity.ok(assignmentRuleService.getAllRules());
    }

    @Operation(summary = "Get Rule by ID")
    @GetMapping("/{ruleId}")
    public ResponseEntity<AssignmentRuleDto> getRuleById(@PathVariable Long ruleId) {
        return ResponseEntity.ok(assignmentRuleService.getRuleById(ruleId));
    }

    @Operation(summary = "Get Rules by Course")
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<AssignmentRuleDto>> getRulesByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(assignmentRuleService.getRulesByCourse(courseId));
    }

    @Operation(summary = "Get Active Rules")
    @GetMapping("/active")
    public ResponseEntity<List<AssignmentRuleDto>> getActiveRules() {
        return ResponseEntity.ok(assignmentRuleService.getActiveRules());
    }

    @Operation(summary = "Update Rule")
    @PutMapping("/{ruleId}")
    public ResponseEntity<AssignmentRuleDto> updateRule(
            @PathVariable Long ruleId,
            @RequestBody AssignmentRuleDto ruleDto) {
        return ResponseEntity.ok(assignmentRuleService.updateRule(ruleId, ruleDto));
    }

    @Operation(summary = "Delete Rule")
    @DeleteMapping("/{ruleId}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long ruleId) {
        assignmentRuleService.deleteRule(ruleId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Preview Rule",
            description = "See which employees will match the rule criteria without creating enrollments")
    @PostMapping("/preview")
    public ResponseEntity<RulePreviewDto> previewRule(@RequestBody AssignmentRuleDto ruleDto) {
        return ResponseEntity.ok(assignmentRuleService.previewRule(ruleDto));
    }

    @Operation(summary = "Execute Rule Manually",
            description = "Manually trigger rule execution to create enrollments")
    @PostMapping("/{ruleId}/execute")
    public ResponseEntity<RuleExecutionResultDto> executeRule(@PathVariable Long ruleId) {
        return ResponseEntity.ok(ruleExecutionService.executeRule(ruleId));
    }


    @PostMapping("/{ruleId}/activate")
    public ResponseEntity<AssignmentRuleDto> activateRule(@PathVariable Long ruleId) {
        return ResponseEntity.ok(assignmentRuleService.activateRule(ruleId));
    }

    @Operation(summary = "Deactivate Rule")
    @PostMapping("/{ruleId}/deactivate")
    public ResponseEntity<AssignmentRuleDto> deactivateRule(@PathVariable Long ruleId) {
        return ResponseEntity.ok(assignmentRuleService.deactivateRule(ruleId));
    }
}