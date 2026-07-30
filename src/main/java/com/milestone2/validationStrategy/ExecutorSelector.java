package com.milestone2.validationStrategy;

import com.milestone2.crossValidation.KFoldExecutor;
import com.milestone2.validationTimeseries.WalkForwardExecutor;

/**
 * Resolves the validation executor that matches the configured strategy.
 */
public class ExecutorSelector {
 private final ValidationExecutor crossValidationExecutor;
 private final ValidationExecutor walkForwardValidationExecutor;
 private final ValidationExecutor orderedHoldoutValidationExecutor;

 public ExecutorSelector() {
 this(new KFoldExecutor(),
 new WalkForwardExecutor(),
 new HoldoutExecutor());
 }

 ExecutorSelector(ValidationExecutor crossValidationExecutor,
 ValidationExecutor walkForwardValidationExecutor,
 ValidationExecutor orderedHoldoutValidationExecutor) {
 this.crossValidationExecutor = crossValidationExecutor;
 this.walkForwardValidationExecutor = walkForwardValidationExecutor;
 this.orderedHoldoutValidationExecutor = orderedHoldoutValidationExecutor;
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
 case ORDERED_HOLDOUT:
 return orderedHoldoutValidationExecutor;
 default:
 throw new IllegalArgumentException("Unsupported validation strategy: " + strategy);
 }
 }
}
