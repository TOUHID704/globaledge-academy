// 🎯 assignment/scheduler/RuleExecutionScheduler.java
package com.globaledge.academy.lms.assignment.scheduler;

import com.globaledge.academy.lms.assignment.service.RuleExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RuleExecutionScheduler {

    private final RuleExecutionService ruleExecutionService;

    /**
     * Execute all daily rules at 2 AM every day
     * Cron format: second minute hour day month weekday
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void executeScheduledRules() {
        log.info("=== Starting scheduled rule execution ===");

        try {
            ruleExecutionService.executeAllScheduledRules();
            log.info("=== Scheduled rule execution completed successfully ===");
        } catch (Exception e) {
            log.error("=== Error during scheduled rule execution: {} ===", e.getMessage(), e);
        }
    }
}