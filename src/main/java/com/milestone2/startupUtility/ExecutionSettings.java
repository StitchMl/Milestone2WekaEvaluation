package com.milestone2.startupUtility;

import com.milestone2.evaluation.BalancingStrategy;
import com.milestone2.evaluation.FeatureSelectionStrategy;
import com.milestone2.validationStrategy.ValidationStrategy;

/**
 * Execution-time settings for one analysis run.
 */
public class ExecutionSettings {
 private final String runId;
 private final int runs;
 private final int folds;
 private final long seed;
 private final int maxParallelism;
 private final PreprocessingConfig preprocessing;
 private final ValidationConfig validation;

 public ExecutionSettings(String runId,
 int runs,
 int folds,
 long seed,
 int maxParallelism,
 PreprocessingConfig preprocessing,
 ValidationConfig validation) {
 this.runId = runId;
 this.runs = runs;
 this.folds = folds;
 this.seed = seed;
 this.maxParallelism = maxParallelism;
 this.preprocessing = preprocessing;
 this.validation = validation;
 }

 /**
 * Returns the identifier assigned to the current analysis run.
 *
 * @return run identifier
 */
 public String getRunId() {
 return runId;
 }

 /**
 * Returns how many repeated validation runs must be executed.
 *
 * @return number of runs
 */
 public int getRuns() {
 return runs;
 }

 /**
 * Returns the number of folds requested for cross-validation.
 *
 * @return configured fold count
 */
 public int getFolds() {
 return folds;
 }

 /**
 * Returns the random seed used to keep validation reproducible.
 *
 * @return deterministic seed
 */
 public long getSeed() {
 return seed;
 }

 /**
 * Returns the maximum number of validation workers requested by the user.
 *
 * @return maximum parallelism, or a non-positive value when automatic resolution is desired
 */
 public int getMaxParallelism() {
 return maxParallelism;
 }

 /**
 * Returns the balancing strategy to apply inside the preprocessing pipeline.
 *
 * @return balancing strategy
 */
 public BalancingStrategy getBalancingStrategy() {
 return preprocessing.getBalancingStrategy();
 }

 /**
 * Returns the feature-selection strategy applied before balancing inside each training fold.
 *
 * @return feature-selection strategy
 */
 public FeatureSelectionStrategy getFeatureSelectionStrategy() {
 return preprocessing.getFeatureSelectionStrategy();
 }

 /**
 * Returns the validation strategy selected for this run.
 *
 * @return validation strategy
 */
 public ValidationStrategy getValidationStrategy() {
 return validation.getValidationStrategy();
 }

 /**
 * Returns the temporal attribute used by walk-forward validation.
 *
 * @return temporal attribute name
 */
 public String getTemporalAttributeName() {
 return validation.getTemporalAttributeName();
 }

 /**
 * Returns the minimum number of temporal periods required in the training window.
 *
 * @return minimum training periods for walk-forward validation
 */
 public int getMinimumTrainingPeriods() {
 return validation.getMinimumTrainingPeriods();
 }
}
