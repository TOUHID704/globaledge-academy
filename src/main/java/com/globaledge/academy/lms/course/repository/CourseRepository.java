package com.globaledge.academy.lms.course.repository;

import com.globaledge.academy.lms.course.entity.Course;
import com.globaledge.academy.lms.course.enums.CourseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course,Long> {

    List<Course> findByIsPublishedTrue();
    List<Course> findByCourseCategory(CourseCategory category);
    Optional<Course> findByCourseIdAndIsPublishedTrue(Long courseId);
    long countByIsPublishedTrue();

}
