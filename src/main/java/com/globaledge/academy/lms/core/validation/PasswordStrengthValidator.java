package com.globaledge.academy.lms.core.validation;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * Validates password strength according to security policies.
 */
@Component
public class PasswordStrengthValidator {

    private static final int MIN_LENGTH = 8;
    private static final String UPPERCASE_PATTERN = ".*[A-Z].*";
    private static final String LOWERCASE_PATTERN = ".*[a-z].*";
    private static final String DIGIT_PATTERN = ".*\\d.*";
    private static final String SPECIAL_CHAR_PATTERN = ".*[@#$%&*!].*";

    /**
     * Validates password strength and returns list of validation errors.
     * Empty list means password is valid.
     */
    public List<String> validate(String password) {
        List<String> errors = new ArrayList<>();

        if (password == null || password.isEmpty()) {
            errors.add("Password cannot be empty");
            return errors;
        }

        if (password.length() < MIN_LENGTH) {
            errors.add("Password must be at least " + MIN_LENGTH + " characters long");
        }

        if (!password.matches(UPPERCASE_PATTERN)) {
            errors.add("Password must contain at least one uppercase letter");
        }

        if (!password.matches(LOWERCASE_PATTERN)) {
            errors.add("Password must contain at least one lowercase letter");
        }

        if (!password.matches(DIGIT_PATTERN)) {
            errors.add("Password must contain at least one digit");
        }

        if (!password.matches(SPECIAL_CHAR_PATTERN)) {
            errors.add("Password must contain at least one special character (@#$%&*!)");
        }

        return errors;
    }

    /**
     * Returns true if password meets all strength requirements.
     */
    public boolean isValid(String password) {
        return validate(password).isEmpty();
    }
}
