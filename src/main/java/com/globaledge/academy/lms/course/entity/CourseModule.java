package com.globaledge.academy.lms.course.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "course_modules")
@Getter
@Setter
@NoArgsConstructor
public class CourseModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseModuleId;

    private String title;

    private Integer moduleNumber;

    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;

    @OneToMany(mappedBy = "courseModule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ModuleContent> moduleContents;

    public void addContent(ModuleContent content) {
        moduleContents.add(content);
        content.setCourseModule(this);
    }


}

