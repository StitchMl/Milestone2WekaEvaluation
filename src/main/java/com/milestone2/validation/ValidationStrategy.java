package com.milestone2.validation;

import java.util.Locale;

/**
 * Supported evaluation strategies for classifier validation.
 */
public enum ValidationStrategy {
    WALK_FORWARD("walk-forward"),
    CROSS_VALIDATION("cross-validation");

    private final String cliValue;

    ValidationStrategy(String cliValue) {
        this.cliValue = cliValue;
    }

    /**
     * Parses a CLI value into a validation strategy.
     *
     * @param raw raw CLI value such as {@code walk-forward}
     * @return parsed strategy
     */
    public static ValidationStrategy from(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Validation strategy cannot be blank");
        }

        String normalized = raw.trim().toLowerCase(Locale.ROOT);
        for (ValidationStrategy strategy : values()) {
            if (strategy.cliValue.equals(normalized)) {
                return strategy;
            }
        }
        throw new IllegalArgumentException("Unsupported validation strategy: " + raw);
    }

    /**
     * Returns the stable CLI-friendly value.
     *
     * @return CLI value for the strategy
     */
    public String getCliValue() {
        return cliValue;
    }
}
