package com.milestone2.analysis;

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

    public AnalysisExecution(String runId,
                             int runs,
                             int folds,
                             long seed,
                             int maxParallelism,
                             boolean applySmote) {
        this.runId = runId;
        this.runs = runs;
        this.folds = folds;
        this.seed = seed;
        this.maxParallelism = maxParallelism;
        this.applySmote = applySmote;
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
}

