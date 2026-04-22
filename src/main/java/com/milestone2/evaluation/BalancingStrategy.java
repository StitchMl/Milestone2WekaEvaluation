package com.milestone2.evaluation;

/**
 * Balancing strategy applied to the training set before classifier training.
 */
public enum BalancingStrategy {
    NONE("none"),
    SMOTE("smote"),
    UNDERSAMPLING("undersampling"),
    OVERSAMPLING("oversampling");

    private final String cliValue;

    BalancingStrategy(String cliValue) {
        this.cliValue = cliValue;
    }

    public String getCliValue() {
        return cliValue;
    }

    public static BalancingStrategy from(String value) {
        for (BalancingStrategy strategy : values()) {
            if (strategy.cliValue.equalsIgnoreCase(value)) {
                return strategy;
            }
        }
        throw new IllegalArgumentException("Unknown balancing strategy: " + value);
    }
}
