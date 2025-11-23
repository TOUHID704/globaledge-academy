package com.globaledge.academy.lms.course.repository;

import com.globaledge.academy.lms.course.entity.CourseModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseModuleRepository extends JpaRepository<CourseModule, Long> {
    List<CourseModule> findByCourse_CourseIdOrderByModuleNumberAsc(Long courseId);
}