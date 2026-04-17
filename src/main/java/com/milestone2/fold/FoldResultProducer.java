package com.milestone2.fold;

import weka.core.Instances;

/**
 * Produces a fold result from one train/test split.
 */
@FunctionalInterface
public interface FoldResultProducer {
    /**
     * Evaluates one train/test split and returns the resulting metrics with context metadata.
     *
     * @param train   training subset
     * @param test    test subset
     * @param context split metadata
     * @return fold evaluation result
     * @throws Exception when split evaluation fails
     */
    PerFoldResult produce(Instances train, Instances test, FoldContext context) throws Exception;
}
