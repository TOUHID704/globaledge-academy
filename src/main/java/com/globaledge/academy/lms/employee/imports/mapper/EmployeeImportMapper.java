package com.globaledge.academy.lms.employee.imports.mapper;


import com.globaledge.academy.lms.employee.entity.Employee;
import com.globaledge.academy.lms.employee.enums.EmployeeStatus;
import com.globaledge.academy.lms.employee.enums.EmploymentType;
import com.globaledge.academy.lms.employee.enums.Gender;
import com.globaledge.academy.lms.employee.enums.WorkMode;
import com.globaledge.academy.lms.employee.imports.model.EmployeeImportRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

/**
 * Maps the raw EmployeeImportRecord DTO to the Employee database entity.
 * Handles data type conversions like parsing strings into dates and enums.
 */
@Slf4j
@Component
public class EmployeeImportMapper {

    private static final List<DateTimeFormatter> DATE_FORMATTERS = Arrays.asList(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd")
    );

    /**
     * Converts an EmployeeImportRecord to an Employee entity.
     * Note: This method does NOT set the manager relationship. That is handled
     * in a second pass by the EmployeeImportService to avoid ordering issues.
     *
     * @param record The raw data record from the Excel file.
     * @param importedBy The user who initiated the import.
     * @return A new Employee entity, ready to be saved.
     */
    public Employee toEntity(EmployeeImportRecord record, String importedBy) {
        return Employee.builder()
                .employeeId(record.getEmployeeId())
                .firstName(record.getFirstName())
                .lastName(record.getLastName())
                .email(record.getEmail())
                .department(record.getDepartment())
                .designation(record.getDesignation())
                .dateOfJoining(parseDate(record.getDateOfJoining()))
                .phoneNumber(record.getPhoneNumber())
                .dateOfBirth(parseDate(record.getDateOfBirth()))
                .gender(parseEnum(record.getGender(), Gender.class))
                .domain(record.getDomain())
                .subDomain(record.getSubDomain())
                .employmentType(parseEnum(record.getEmploymentType(), EmploymentType.class))
                .officeLocation(record.getOfficeLocation())
                .workMode(parseEnum(record.getWorkMode(), WorkMode.class))
                .status(parseEnum(record.getStatus(), EmployeeStatus.class, EmployeeStatus.ACTIVE))
                .importedBy(importedBy)
                .build();
    }

    /**
     * Tries to parse a date string using multiple common formats.
     * @param dateStr The date as a string.
     * @return A LocalDate object, or null if parsing fails.
     */
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException e) { /* try next format */ }
        }
        log.warn("Could not parse date string '{}'", dateStr);
        return null;
    }

    /**
     * Tries to parse a string value into a given enum type, ignoring case and spaces.
     * @param value The string value from the Excel file.
     * @param enumClass The target enum class.
     * @param defaultValue The value to return if parsing fails.
     * @return The matching enum constant, or the default value if not found.
     */
    private <E extends Enum<E>> E parseEnum(String value, Class<E> enumClass, E defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            // Replace spaces with underscores for better enum matching (e.g., "FULL TIME" -> FULL_TIME)
            return Enum.valueOf(enumClass, value.trim().toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid enum value '{}' for enum class {}: {}", value, enumClass.getSimpleName(), e.getMessage());
            return defaultValue;
        }
    }

    /**
     * Overloaded helper method for enums without a default value.
     */
    private <E extends Enum<E>> E parseEnum(String value, Class<E> enumClass) {
        return parseEnum(value, enumClass, null);
    }
}