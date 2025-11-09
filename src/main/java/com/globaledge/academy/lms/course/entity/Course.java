package com.globaledge.academy.lms.course.entity;

import com.globaledge.academy.lms.course.enums.CourseCategory;
import com.globaledge.academy.lms.course.enums.DifficultyLevel;
import com.globaledge.academy.lms.employee.enums.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @OneToMany(mappedBy = "course")
    private List<CourseModule> modules;




}


    /*
      for example a course will look like
      course01
      course02
      course03

      so each course will have many modules
      cours01
       -module01
       -module02

       so the relationships become like one course will have many modules.
     */
