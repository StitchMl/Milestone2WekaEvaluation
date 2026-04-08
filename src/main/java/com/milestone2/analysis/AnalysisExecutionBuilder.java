package com.milestone2.analysis;

/**
 * Collects CLI values that affect runtime execution.
 */
public class AnalysisExecutionBuilder {
    private int runs = Config.DEFAULT_RUNS;
    private int folds = Config.DEFAULT_FOLDS;
    private long seed = Config.DEFAULT_SEED;
    private int maxParallelism = Config.DEFAULT_MAX_PARALLELISM;
    private boolean applySmote = Config.DEFAULT_APPLY_SMOTE;

    public boolean apply(CliArgument argument) {
        switch (argument.getKey()) {
            case "runs":
                runs = Integer.parseInt(argument.getValue());
                return true;
            case "folds":
                folds = Integer.parseInt(argument.getValue());
                return true;
            case "seed":
                seed = Long.parseLong(argument.getValue());
                return true;
            case "threads":
                maxParallelism = Integer.parseInt(argument.getValue());
                return true;
            case "smote":
                applySmote = Boolean.parseBoolean(argument.getValue());
                return true;
            default:
                return false;
        }
    }

    public AnalysisExecution build(String runId) {
        return new AnalysisExecution(runId, runs, folds, seed, maxParallelism, applySmote);
    }
}

