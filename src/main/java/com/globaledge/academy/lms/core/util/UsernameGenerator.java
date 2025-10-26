package com.globaledge.academy.lms.core.util;

import org.springframework.stereotype.Component;
import java.util.Locale;

/**
 * Utility class for generating usernames from employee information.
 */
@Component
public class UsernameGenerator {

    /**
     * Generates a username in format: firstname.lastname
     * Handles special characters and ensures lowercase.
     */
    public String generateUsername(String firstName, String lastName) {
        if (firstName == null || lastName == null ||
                firstName.trim().isEmpty() || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name and last name must not be empty");
        }

        String username = firstName.trim().toLowerCase(Locale.ROOT) + "." +
                lastName.trim().toLowerCase(Locale.ROOT);

        // Remove special characters and spaces
        return username.replaceAll("[^a-z0-9.]", "");
    }

    /**
     * Generates a username with numeric suffix to handle duplicates.
     */
    public String generateUsername(String firstName, String lastName, int suffix) {
        return generateUsername(firstName, lastName) + suffix;
    }
}
