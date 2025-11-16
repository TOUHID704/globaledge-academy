// 🎯 assignment/service/impl/RuleEvaluationServiceImpl.java
package com.globaledge.academy.lms.assignment.serviceImpl;

import com.globaledge.academy.lms.assignment.dto.*;
import com.globaledge.academy.lms.assignment.entity.*;
import com.globaledge.academy.lms.assignment.enums.FieldOperator;
import com.globaledge.academy.lms.assignment.service.RuleEvaluationService;
import com.globaledge.academy.lms.employee.entity.Employee;
import com.globaledge.academy.lms.employee.repository.EmployeeRepository;
import com.globaledge.academy.lms.enrollment.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RuleEvaluationServiceImpl implements RuleEvaluationService {

    private final EmployeeRepository employeeRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final EntityManager entityManager;

    @Override
    public List<Employee> findMatchingEmployees(CourseAssignmentRule rule) {
        log.info("Finding matching employees for rule: {}", rule.getRuleName());

        if (rule.getCriteria() == null || rule.getCriteria().isEmpty()) {
            log.warn("No criteria defined for rule: {}", rule.getRuleName());
            return Collections.emptyList();
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Employee> query = cb.createQuery(Employee.class);
        Root<Employee> root = query.from(Employee.class);

        List<Predicate> predicates = new ArrayList<>();

        // Build predicates from criteria
        for (RuleCriterion criterion : rule.getCriteria()) {
            Predicate predicate = buildPredicate(cb, root, criterion);
            if (predicate != null) {
                predicates.add(predicate);
            }
        }

        if (predicates.isEmpty()) {
            log.warn("No valid predicates created for rule: {}", rule.getRuleName());
            return Collections.emptyList();
        }

        // Combine predicates with AND or OR logic
        Predicate finalPredicate;
        if ("OR".equalsIgnoreCase(rule.getMatchLogic())) {
            finalPredicate = cb.or(predicates.toArray(new Predicate[0]));
            log.debug("Using OR logic for rule: {}", rule.getRuleName());
        } else {
            finalPredicate = cb.and(predicates.toArray(new Predicate[0]));
            log.debug("Using AND logic for rule: {}", rule.getRuleName());
        }

        query.where(finalPredicate);

        List<Employee> matchedEmployees = entityManager.createQuery(query).getResultList();
        log.info("Found {} matching employees for rule: {}", matchedEmployees.size(), rule.getRuleName());

        return matchedEmployees;
    }

    @Override
    public RulePreviewDto previewRule(CourseAssignmentRule rule, Long courseId) {
        log.info("Previewing rule for course: {}", courseId);

        List<Employee> matchedEmployees = findMatchingEmployees(rule);

        List<MatchedEmployeeDto> employeeDtos = matchedEmployees.stream()
                .map(emp -> {
                    boolean alreadyEnrolled = enrollmentRepository
                            .existsByEmployee_IdAndCourse_CourseId(emp.getId(), courseId);

                    return MatchedEmployeeDto.builder()
                            .employeeId(emp.getId())
                            .employeeIdString(emp.getEmployeeId())
                            .firstName(emp.getFirstName())
                            .lastName(emp.getLastName())
                            .email(emp.getEmail())
                            .department(emp.getDepartment())
                            .designation(emp.getDesignation())
                            .location(emp.getOfficeLocation())
                            .alreadyEnrolled(alreadyEnrolled)
                            .build();
                })
                .collect(Collectors.toList());

        long alreadyEnrolledCount = employeeDtos.stream()
                .filter(MatchedEmployeeDto::isAlreadyEnrolled)
                .count();

        return RulePreviewDto.builder()
                .totalMatched(matchedEmployees.size())
                .alreadyEnrolled((int) alreadyEnrolledCount)
                .willBeEnrolled(matchedEmployees.size() - (int) alreadyEnrolledCount)
                .matchedEmployees(employeeDtos)
                .build();
    }

    private Predicate buildPredicate(CriteriaBuilder cb, Root<Employee> root, RuleCriterion criterion) {
        String fieldName = criterion.getFieldName();
        String fieldValue = criterion.getFieldValue();
        FieldOperator operator = criterion.getOperator();

        if (fieldValue == null || fieldValue.trim().isEmpty()) {
            log.warn("Empty field value for criterion: {}", fieldName);
            return null;
        }

        try {
            Path<String> path = root.get(fieldName);

            switch (operator) {
                case EQUALS:
                    return cb.equal(cb.lower(path), fieldValue.toLowerCase().trim());

                case NOT_EQUALS:
                    return cb.notEqual(cb.lower(path), fieldValue.toLowerCase().trim());

                case CONTAINS:
                    return cb.like(cb.lower(path), "%" + fieldValue.toLowerCase().trim() + "%");

                case NOT_CONTAINS:
                    return cb.notLike(cb.lower(path), "%" + fieldValue.toLowerCase().trim() + "%");

                case IN:
                    List<String> values = Arrays.stream(fieldValue.split(","))
                            .map(String::trim)
                            .map(String::toLowerCase)
                            .collect(Collectors.toList());
                    return cb.lower(path).in(values);

                case NOT_IN:
                    List<String> notInValues = Arrays.stream(fieldValue.split(","))
                            .map(String::trim)
                            .map(String::toLowerCase)
                            .collect(Collectors.toList());
                    return cb.not(cb.lower(path).in(notInValues));

                case GREATER_THAN:
                    if ("dateOfJoining".equals(fieldName) || "dateOfBirth".equals(fieldName)) {
                        Path<LocalDate> datePath = root.get(fieldName);
                        return cb.greaterThan(datePath, LocalDate.parse(fieldValue.trim()));
                    }
                    return cb.greaterThan(path, fieldValue.trim());

                case LESS_THAN:
                    if ("dateOfJoining".equals(fieldName) || "dateOfBirth".equals(fieldName)) {
                        Path<LocalDate> datePath = root.get(fieldName);
                        return cb.lessThan(datePath, LocalDate.parse(fieldValue.trim()));
                    }
                    return cb.lessThan(path, fieldValue.trim());

                case GREATER_THAN_EQUAL:
                    if ("dateOfJoining".equals(fieldName) || "dateOfBirth".equals(fieldName)) {
                        Path<LocalDate> datePath = root.get(fieldName);
                        return cb.greaterThanOrEqualTo(datePath, LocalDate.parse(fieldValue.trim()));
                    }
                    return cb.greaterThanOrEqualTo(path, fieldValue.trim());

                case LESS_THAN_EQUAL:
                    if ("dateOfJoining".equals(fieldName) || "dateOfBirth".equals(fieldName)) {
                        Path<LocalDate> datePath = root.get(fieldName);
                        return cb.lessThanOrEqualTo(datePath, LocalDate.parse(fieldValue.trim()));
                    }
                    return cb.lessThanOrEqualTo(path, fieldValue.trim());

                default:
                    log.warn("Unsupported operator: {}", operator);
                    return null;
            }
        } catch (Exception e) {
            log.error("Error building predicate for field: {}, operator: {}, value: {}",
                    fieldName, operator, fieldValue, e);
            return null;
        }
    }
}