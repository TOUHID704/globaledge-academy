package com.globaledge.academy.lms.user.entity;

import com.globaledge.academy.lms.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users", indexes = {
        @Index(name = "idx_username", columnList = "username"),
        @Index(name = "idx_email", columnList = "email"),
        @Index(name = "idx_employee_id", columnList = "employee_id")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserRole userRole = UserRole.USER;

    // Link to Employee entity via employeeId (soft reference, no FK constraint)
    @Column(name = "employee_id", unique = true)
    private String employeeId;

    @Column(nullable = false)
    @Builder.Default
    private Boolean accountLocked = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean accountEnabled = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean passwordExpired = true; // Force first-time password change

    @Column(nullable = false)
    @Builder.Default
    private Integer failedLoginAttempts = 0;

    private LocalDateTime lastPasswordChangeDate;

    private LocalDateTime accountLockedUntil;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Helper methods
    public void incrementFailedAttempts() {
        this.failedLoginAttempts++;
    }

    public void resetFailedAttempts() {
        this.failedLoginAttempts = 0;
    }

    public void lockAccount(int durationMinutes) {
        this.accountLocked = true;
        this.accountLockedUntil = LocalDateTime.now().plusMinutes(durationMinutes);
    }

    public void unlockAccount() {
        this.accountLocked = false;
        this.accountLockedUntil = null;
        this.failedLoginAttempts = 0;
    }

    public boolean isAccountLockedTemporarily() {
        if (!accountLocked || accountLockedUntil == null) {
            return false;
        }
        if (LocalDateTime.now().isAfter(accountLockedUntil)) {
            // Auto-unlock if lock duration has passed
            unlockAccount();
            return false;
        }
        return true;
    }
}