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

    @OneToMany(mappedBy = "courseModule")
    private List<ModuleContent> moduleContents;






}

/*

 course Modules.
  Module01
  Module02
  Module03

  see this all modules will belong to one unique course.

  so this moudles would have something to refere to a course.

  so it would be a course that's here a course would be linked to this moudles.
 */


/*
Now for module content.

each module will have different videos.
module01
 -vidoe01
 -video02
 -video03

 simalry for for module02

 so one module is having many videos.



 */
