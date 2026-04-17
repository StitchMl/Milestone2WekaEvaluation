package com.milestone2.dataset;

import com.milestone2.analysis.AnalysisConfig;
import com.milestone2.validation.ValidationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Validates whether a dataset is ready for classifier evaluation.
 */
public class DatasetValidationService {
    private static final Logger log = LoggerFactory.getLogger(DatasetValidationService.class);

    /**
     * Validates structural dataset constraints before model evaluation starts.
     *
     * @param data   dataset to validate
     * @param config immutable analysis configuration
     */
    public void validate(Instances data, AnalysisConfig config) {
        if (data.classIndex() < 0) {
            throw new IllegalArgumentException("Dataset '" + data.relationName() + "' has no class attribute configured");
        }
        if (!data.classAttribute().isNominal()) {
            throw new IllegalArgumentException(
                    "Class attribute '" + data.classAttribute().name() + "' must be nominal for classification"
            );
        }
        validateStrategySpecificConstraints(data, config);
    }

    /**
     * Applies validation rules that depend on the selected validation strategy.
     *
     * @param data   dataset to validate
     * @param config immutable analysis configuration
     */
    private void validateStrategySpecificConstraints(Instances data, AnalysisConfig config) {
        if (config.getExecution().getValidationStrategy() != ValidationStrategy.CROSS_VALIDATION) {
            if (data.numInstances() < 2) {
                throw new IllegalArgumentException(
                        "Dataset '" + data.relationName() + "' must contain at least two instances"
                );
            }
            return;
        }

        int folds = config.getExecution().getFolds();
        if (folds < 2) {
            throw new IllegalArgumentException("The number of folds must be at least 2");
        }
        if (data.numInstances() < folds) {
            throw new IllegalArgumentException(
                    "Dataset '" + data.relationName() + "' has fewer instances than folds: "
                            + data.numInstances() + " < " + folds
            );
        }

        warnIfMinorityClassIsSmallerThanFolds(data, folds);
    }

    /**
     * Logs a warning when the smallest class has fewer instances than the requested folds.
     *
     * @param data  dataset being validated
     * @param folds configured fold count
     */
    private void warnIfMinorityClassIsSmallerThanFolds(Instances data, int folds) {
        int[] classCounts = new int[data.numClasses()];
        for (Instance instance : data) {
            classCounts[(int) instance.classValue()]++;
        }

        int smallestClassCount = data.numInstances();
        for (int count : classCounts) {
            smallestClassCount = Math.min(smallestClassCount, count);
        }

        if (smallestClassCount < folds) {
            log.warn(
                    "Dataset '{}' has only {} instances in the smallest class with {} folds; "
                            + "some folds may miss that class and produce less reliable metrics",
                    data.relationName(),
                    smallestClassCount,
                    folds
            );
        }
    }
}

