package com.milestone2.evaluation;

import com.milestone2.analysis.AnalysisConfig;
import weka.core.Attribute;

import java.util.Arrays;
import java.util.List;

/**
 * Resolves the positive class for binary classification metrics.
 */
public class PositiveClassResolver {
    private static final List<String> POSITIVE_LABEL_CANDIDATES = Arrays.asList(
            "buggy", "yes", "true", "positive", "defective", "1"
    );

    public String resolvePositiveClassValue(Attribute classAttribute, AnalysisConfig config) {
        return classAttribute.value(resolvePositiveClassIndex(classAttribute, config));
    }

    public int resolvePositiveClassIndex(Attribute classAttribute, AnalysisConfig config) {
        String configuredPositiveClass = config.getSelection().getPositiveClassValue();
        if (configuredPositiveClass != null) {
            int configuredIndex = classAttribute.indexOfValue(configuredPositiveClass);
            if (configuredIndex < 0) {
                throw new IllegalArgumentException(
                        "Positive class '" + configuredPositiveClass
                                + "' not found in attribute '" + classAttribute.name() + "'"
                );
            }
            return configuredIndex;
        }

        for (String candidate : POSITIVE_LABEL_CANDIDATES) {
            int index = classAttribute.indexOfValue(candidate);
            if (index >= 0) {
                return index;
            }
        }

        if (classAttribute.numValues() == 2) {
            return classAttribute.numValues() - 1;
        }

        throw new IllegalArgumentException(
                "Unable to infer the positive class for attribute '" + classAttribute.name()
                        + "'. Pass --positive-class=<value> explicitly."
        );
    }
}


