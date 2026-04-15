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

    public String getRunId() {
        return runId;
    }

    public int getRuns() {
        return runs;
    }

    public int getFolds() {
        return folds;
    }

    public long getSeed() {
        return seed;
    }

    public int getMaxParallelism() {
        return maxParallelism;
    }

    public boolean isApplySmote() {
        return applySmote;
    }

    public ValidationStrategy getValidationStrategy() {
        return validationStrategy;
    }

    public String getTemporalAttributeName() {
        return temporalAttributeName;
    }

    public int getMinimumTrainingPeriods() {
        return minimumTrainingPeriods;
    }
}

