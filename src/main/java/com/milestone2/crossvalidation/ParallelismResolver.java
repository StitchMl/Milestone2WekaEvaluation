package com.milestone2.crossValidation;

import com.milestone2.startupUtility.ExecutionSettings;

import java.util.function.IntSupplier;

/**
 * Resolves how many fold workers can run concurrently for one cross-validation run.
 */
public class ParallelismResolver {
 private final IntSupplier availableProcessorsSupplier;

 public ParallelismResolver() {
 this(() -> Runtime.getRuntime().availableProcessors());
 }

 public ParallelismResolver(IntSupplier availableProcessorsSupplier) {
 this.availableProcessorsSupplier = availableProcessorsSupplier;
 }

 /**
 * Resolves the effective worker count by combining user preference, CPU availability and fold count.
 *
 * @param execution execution settings
 * @return parallel fold worker count
 */
 public int resolve(ExecutionSettings execution) {
 int requestedParallelism = execution.getMaxParallelism();
 int automaticParallelism = Math.max(1, availableProcessorsSupplier.getAsInt() - 1);
 int cappedParallelism = requestedParallelism > 0 ? requestedParallelism : automaticParallelism;
 return Math.max(1, Math.min(execution.getFolds(), cappedParallelism));
 }
}

