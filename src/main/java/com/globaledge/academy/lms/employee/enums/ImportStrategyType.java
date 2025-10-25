package com.globaledge.academy.lms.employee.enums;

import java.util.stream.Stream;

/**
 * Defines the available import strategies and maps them to codes used in the Excel file.
 * This provides a type-safe way to handle different import behaviors.
 */
public enum ImportStrategyType {
    CREATE_ONLY("101", "createOnlyImportStrategy"),
    UPDATE_EXISTING("102", "updateExistingImportStrategy"),
    UPSERT("103", "upsertImportStrategy");

    private final String code;
    private final String beanName;

    ImportStrategyType(String code, String beanName) {
        this.code = code;
        this.beanName = beanName;
    }

    public String getCode() {
        return code;
    }

    public String getBeanName() {
        return beanName;
    }

    /**
     * Finds an ImportStrategyType enum constant from a string code.
     * @param code The string code (e.g., "101").
     * @return The matching enum constant, or null if not found.
     */
    public static ImportStrategyType fromCode(String code) {
        if (code == null) {
            return null;
        }
        return Stream.of(ImportStrategyType.values())
                .filter(c -> c.getCode().equals(code.trim()))
                .findFirst()
                .orElse(null);
    }
}