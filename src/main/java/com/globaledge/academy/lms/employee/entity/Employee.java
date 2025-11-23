package com.globaledge.academy.lms.employee.entity;

import com.globaledge.academy.lms.employee.enums.EmployeeStatus;
import com.globaledge.academy.lms.employee.enums.EmploymentType;
import com.globaledge.academy.lms.employee.enums.Gender;
import com.globaledge.academy.lms.employee.enums.WorkMode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "employees", indexes = {
        @Index(name = "idx_employee_id", columnList = "employeeId", unique = true),
        @Index(name = "idx_email", columnList = "email", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String employeeId;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 100)
    private String department;

    @Column(nullable = false, length = 100)
    private String designation;

    @Column(nullable = false)
    private LocalDate dateOfJoining;

    @Column(length = 20)
    private String phoneNumber;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Gender gender;

    @Column(length = 100)
    private String domain;

    @Column(length = 100)
    private String subDomain;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EmploymentType employmentType;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_employee_id", referencedColumnName = "employeeId")
    private Employee manager;


    @OneToMany(mappedBy = "manager")
    private Set<Employee> subordinates;


    @Column(length = 100)
    private String officeLocation;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private WorkMode workMode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EmployeeStatus status = EmployeeStatus.ACTIVE;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(length = 50)
    private String importedBy;
}