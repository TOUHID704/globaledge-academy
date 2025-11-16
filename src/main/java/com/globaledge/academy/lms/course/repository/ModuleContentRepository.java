package com.globaledge.academy.lms.course.repository;

import com.globaledge.academy.lms.course.entity.ModuleContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ModuleContentRepository extends JpaRepository<ModuleContent, Long> {
    List<ModuleContent> findByCourseModule_CourseModuleIdOrderByContentNumberAsc(Long moduleId);
}