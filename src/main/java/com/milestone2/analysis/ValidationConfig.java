package com.milestone2.analysis;

import com.milestone2.validation.ValidationStrategy;

/**
 * Groups the three validation-related settings so that {@link AnalysisExecution} stays within
 * the seven-parameter constructor limit recommended by static-analysis rules.
 */
public class ValidationConfig {
 private final ValidationStrategy validationStrategy;
 private final String temporalAttributeName;
 private final int minimumTrainingPeriods;

 public ValidationConfig(ValidationStrategy validationStrategy,
 String temporalAttributeName,
 int minimumTrainingPeriods) {
 this.validationStrategy = validationStrategy;
 this.temporalAttributeName = temporalAttributeName;
 this.minimumTrainingPeriods = minimumTrainingPeriods;
 }

 /**
 * Returns the validation strategy selected for this run.
 *
 * @return validation strategy
 */
 public ValidationStrategy getValidationStrategy() {
 return validationStrategy;
 }

 /**
 * Returns the temporal attribute used by walk-forward validation.
 *
 * @return temporal attribute name
 */
 public String getTemporalAttributeName() {
 return temporalAttributeName;
 }

 /**
 * Returns the minimum number of temporal periods required in the training window.
 *
 * @return minimum training periods for walk-forward validation
 */
 public int getMinimumTrainingPeriods() {
 return minimumTrainingPeriods;
 }
}
