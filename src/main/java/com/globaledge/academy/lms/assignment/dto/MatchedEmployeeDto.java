// 🎯 assignment/dto/MatchedEmployeeDto.java
package com.globaledge.academy.lms.assignment.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchedEmployeeDto {
    private Long employeeId;
    private String employeeIdString;
    private String firstName;
    private String lastName;
    private String email;
    private String department;
    private String designation;
    private String location;
    private boolean alreadyEnrolled;
}