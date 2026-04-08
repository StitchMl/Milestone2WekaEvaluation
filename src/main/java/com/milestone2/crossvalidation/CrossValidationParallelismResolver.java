package com.milestone2.crossvalidation;

import com.milestone2.analysis.AnalysisExecution;

import java.util.function.IntSupplier;

/**
 * Resolves how many fold workers can run concurrently for one cross-validation run.
 */
public class CrossValidationParallelismResolver {
    private final IntSupplier availableProcessorsSupplier;

    public CrossValidationParallelismResolver() {
        this(() -> Runtime.getRuntime().availableProcessors());
    }

    public CrossValidationParallelismResolver(IntSupplier availableProcessorsSupplier) {
        this.availableProcessorsSupplier = availableProcessorsSupplier;
    }

    public int resolve(AnalysisExecution execution) {
        int requestedParallelism = execution.getMaxParallelism();
        int automaticParallelism = Math.max(1, availableProcessorsSupplier.getAsInt() - 1);
        int cappedParallelism = requestedParallelism > 0 ? requestedParallelism : automaticParallelism;
        return Math.max(1, Math.min(execution.getFolds(), cappedParallelism));
    }
}

