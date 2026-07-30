package com.milestone2.validationStrategy;

import com.milestone2.startupUtility.RunConfig;
import com.milestone2.foldMetadata.FoldResultProducer;
import com.milestone2.foldMetadata.FoldResult;
import weka.core.Instances;

import java.util.List;

/**
 * Executes one concrete validation strategy and returns fold-level metrics.
 */
@SuppressWarnings("java:S112") // Weka classifier API forces generic Exception in execute(); cannot narrow without wrapping
public interface ValidationExecutor {

 /**
 * Identifies the strategy handled by the executor.
 *
 * @return supported validation strategy
 */
 @SuppressWarnings("unused")
 ValidationStrategy supportedStrategy();

 /**
 * Executes the validation flow for the provided dataset.
 *
 * @param data dataset to evaluate
 * @param config immutable runtime configuration
 * @param producer fold evaluator callback
 * @return collected per-split results
 * @throws Exception when a split cannot be evaluated
 */
 List<FoldResult> execute(Instances data,
 RunConfig config,
 FoldResultProducer producer) throws Exception;
}
