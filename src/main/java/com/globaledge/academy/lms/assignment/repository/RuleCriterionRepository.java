// 🎯 assignment/repository/RuleCriterionRepository.java
package com.globaledge.academy.lms.assignment.repository;

import com.globaledge.academy.lms.assignment.entity.RuleCriterion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleCriterionRepository extends JpaRepository<RuleCriterion, Long> {

    /**
     * Find all criteria for a specific rule
     */
    List<RuleCriterion> findByAssignmentRule_RuleIdOrderByCriterionOrderAsc(Long ruleId);

    /**
     * Delete all criteria for a rule
     */
    void deleteByAssignmentRule_RuleId(Long ruleId);

    /**
     * Count criteria for a rule
     */
    long countByAssignmentRule_RuleId(Long ruleId);
}