package com.milestone2.foldMetadata;

import weka.core.Instances;

/**
 * Produces a fold result from one train/test split.
 */
@FunctionalInterface
@SuppressWarnings("java:S112") // Weka classifier API forces generic Exception; narrowing requires wrapping every lambda
public interface FoldResultProducer {
 /**
 * Evaluates one train/test split and returns the resulting metrics with context metadata.
 *
 * @param train training subset
 * @param test test subset
 * @param context split metadata
 * @return fold evaluation result
 * @throws Exception when Weka classifier training or evaluation fails
 */
 FoldResult produce(Instances train, Instances test, FoldContext context) throws Exception;
}
