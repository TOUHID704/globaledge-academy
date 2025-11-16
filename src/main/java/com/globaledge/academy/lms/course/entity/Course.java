package com.globaledge.academy.lms.course.entity;

import com.globaledge.academy.lms.course.enums.CourseCategory;
import com.globaledge.academy.lms.course.enums.DifficultyLevel;
import com.globaledge.academy.lms.employee.enums.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
public class Course {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long courseId;

    private String title;

    private String description;

    private String thumbnailUrl;

    private CourseCategory courseCategory;

    private boolean isPublished = false;

    private String instructor;

    private Integer estimatedDuration;

    private LocalDateTime createdAt;

    private LocalDateTime publishedAt;

    private String createdBy;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseModule> modules;

    public void addModule(CourseModule module) {
        modules.add(module);
        module.setCourse(this);
    }

}

