package com.milestone2.dataset;

import com.milestone2.analysis.AnalysisConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Validates whether a dataset is ready for classifier evaluation.
 */
public class DatasetValidationService {
    private static final Logger log = LoggerFactory.getLogger(DatasetValidationService.class);

    public void validate(Instances data, AnalysisConfig config) {
        int folds = config.getExecution().getFolds();

        if (data.classIndex() < 0) {
            throw new IllegalArgumentException("Dataset '" + data.relationName() + "' has no class attribute configured");
        }
        if (!data.classAttribute().isNominal()) {
            throw new IllegalArgumentException(
                    "Class attribute '" + data.classAttribute().name() + "' must be nominal for classification"
            );
        }
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

