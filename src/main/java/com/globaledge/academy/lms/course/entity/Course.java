package com.globaledge.academy.lms.course.entity;

import com.globaledge.academy.lms.course.enums.CourseCategory;
import com.globaledge.academy.lms.course.enums.CourseStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;

    private String title;

    @Column(length = 4000)
    private String description;

    @Column(name = "thumbnail_media_id")
    private Long thumbnailMediaId;

    @Transient
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    private CourseCategory courseCategory;

    @Enumerated(EnumType.STRING)
    private CourseStatus courseStatus = CourseStatus.DRAFT;

    private String instructor;

    private Integer estimatedDuration;

    private LocalDateTime createdAt;

    private LocalDateTime publishedAt;

    private String createdBy;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseModule> modules = new ArrayList<>();


    public void addModule(CourseModule module) {
        module.setCourse(this);
        this.modules.add(module);
    }

    public void removeModule(CourseModule module) {
        module.setCourse(null);
        this.modules.remove(module);
    }
}
