package com.milestone2.fold;

import weka.core.Instances;

/**
 * Produces a fold result from one train/test split.
 */
@FunctionalInterface
public interface FoldResultProducer {
    PerFoldResult produce(Instances train, Instances test, FoldContext context) throws Exception;
}
