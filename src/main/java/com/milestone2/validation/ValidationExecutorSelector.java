package com.milestone2.validation;

import com.milestone2.crossvalidation.CrossValidationExecutor;
import com.milestone2.validation.timeseries.WalkForwardValidationExecutor;

/**
 * Resolves the validation executor that matches the configured strategy.
 */
public class ValidationExecutorSelector {
    private final ValidationExecutor crossValidationExecutor;
    private final ValidationExecutor walkForwardValidationExecutor;

    public ValidationExecutorSelector() {
        this(new CrossValidationExecutor(), new WalkForwardValidationExecutor());
    }

    ValidationExecutorSelector(ValidationExecutor crossValidationExecutor,
                               ValidationExecutor walkForwardValidationExecutor) {
        this.crossValidationExecutor = crossValidationExecutor;
        this.walkForwardValidationExecutor = walkForwardValidationExecutor;
    }

    /**
     * Selects the executor for the requested strategy.
     *
     * @param strategy strategy requested by configuration
     * @return matching executor
     */
    public ValidationExecutor select(ValidationStrategy strategy) {
        switch (strategy) {
            case CROSS_VALIDATION:
                return crossValidationExecutor;
            case WALK_FORWARD:
                return walkForwardValidationExecutor;
            default:
                throw new IllegalArgumentException("Unsupported validation strategy: " + strategy);
        }
    }
}
