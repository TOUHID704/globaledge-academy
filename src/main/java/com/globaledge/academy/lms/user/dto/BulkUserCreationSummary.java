package com.globaledge.academy.lms.user.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkUserCreationSummary {

    @Builder.Default
    private Integer totalEmployees = 0;

    @Builder.Default
    private Integer usersCreated = 0;

    @Builder.Default
    private Integer usersSkipped = 0;

    @Builder.Default
    private Integer usersFailed = 0;

    @Builder.Default
    private List<String> createdUsernames = new ArrayList<>();

    @Builder.Default
    private List<String> skippedEmployeeIds = new ArrayList<>();

    @Builder.Default
    private List<FailureDetail> failures = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FailureDetail {
        private String employeeId;
        private String email;
        private String reason;
    }

    public void addCreatedUsername(String username) {
        this.createdUsernames.add(username);
        this.usersCreated++;
    }

    public void addSkippedEmployee(String employeeId) {
        this.skippedEmployeeIds.add(employeeId);
        this.usersSkipped++;
    }

    public void addFailure(String employeeId, String email, String reason) {
        this.failures.add(new FailureDetail(employeeId, email, reason));
        this.usersFailed++;
    }
}