package com.globaledge.academy.lms.employee.imports.model;

import com.globaledge.academy.lms.employee.enums.ImportStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Tracks the metadata and statistics for each bulk employee import job.
 * Each record corresponds to one uploaded file.
 */
@Entity
@Table(name = "employee_import_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeImportHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String fileName;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private Integer totalRecords;

    @Column(nullable = false)
    private Integer successCount;

    @Column(nullable = false)
    private Integer errorCount;

    @Column(nullable = false)
    private Integer warningCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ImportStatus status;

    @Column(length = 512)
    private String logFilePath;

    @Column(length = 255)
    private String logFileName;

    @Column(length = 100)
    private String importedBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime importedAt;

    @Lob
    private String errorSummary;
}