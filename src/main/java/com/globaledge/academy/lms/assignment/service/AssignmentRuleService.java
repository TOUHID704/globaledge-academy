// 🎯 assignment/service/AssignmentRuleService.java
package com.globaledge.academy.lms.assignment.service;

import com.globaledge.academy.lms.assignment.dto.*;

import java.util.List;

public interface AssignmentRuleService {
    AssignmentRuleDto createRule(AssignmentRuleDto ruleDto);
    AssignmentRuleDto updateRule(Long ruleId, AssignmentRuleDto ruleDto);
    AssignmentRuleDto getRuleById(Long ruleId);
    List<AssignmentRuleDto> getAllRules();
    List<AssignmentRuleDto> getActiveRules();
    List<AssignmentRuleDto> getRulesByCourse(Long courseId);
    void deleteRule(Long ruleId);
    AssignmentRuleDto activateRule(Long ruleId);
    AssignmentRuleDto deactivateRule(Long ruleId);
    RulePreviewDto previewRule(AssignmentRuleDto ruleDto);
}