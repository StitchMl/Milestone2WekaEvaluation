package com.milestone2.validation;

import com.milestone2.analysis.AnalysisConfig;
import com.milestone2.fold.FoldResultProducer;
import com.milestone2.fold.PerFoldResult;
import weka.core.Instances;

import java.util.List;

/**
 * Executes one concrete validation strategy and returns fold-level metrics.
 */
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
     * @param data     dataset to evaluate
     * @param config   immutable runtime configuration
     * @param producer fold evaluator callback
     * @return collected per-split results
     * @throws Exception when a split cannot be evaluated
     */
    List<PerFoldResult> execute(Instances data,
                                AnalysisConfig config,
                                FoldResultProducer producer) throws Exception;
}
