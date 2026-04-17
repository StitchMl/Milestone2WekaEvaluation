package com.milestone2.analysis;

import com.milestone2.validation.ValidationStrategy;

/**
 * Execution-time settings for one analysis run.
 */
public class AnalysisExecution {
    private final String runId;
    private final int runs;
    private final int folds;
    private final long seed;
    private final int maxParallelism;
    private final boolean applySmote;
    private final ValidationStrategy validationStrategy;
    private final String temporalAttributeName;
    private final int minimumTrainingPeriods;

    public AnalysisExecution(String runId,
                             int runs,
                             int folds,
                             long seed,
                             int maxParallelism,
                             boolean applySmote,
                             ValidationStrategy validationStrategy,
                             String temporalAttributeName,
                             int minimumTrainingPeriods) {
        this.runId = runId;
        this.runs = runs;
        this.folds = folds;
        this.seed = seed;
        this.maxParallelism = maxParallelism;
        this.applySmote = applySmote;
        this.validationStrategy = validationStrategy;
        this.temporalAttributeName = temporalAttributeName;
        this.minimumTrainingPeriods = minimumTrainingPeriods;
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
     * Returns whether SMOTE must be enabled inside the preprocessing pipeline.
     *
     * @return {@code true} when SMOTE should be applied
     */
    public boolean isApplySmote() {
        return applySmote;
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

