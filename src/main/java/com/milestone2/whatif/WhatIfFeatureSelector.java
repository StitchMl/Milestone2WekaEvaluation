package com.milestone2.whatif;

import com.milestone2.analysis.Config;
import com.milestone2.feature.FeatureCorrelation;
import weka.core.Attribute;
import weka.core.Instances;

import java.util.List;

/**
 * Chooses the feature to manipulate for the what-if analysis.
 */
public class WhatIfFeatureSelector {
    /**
     * Selects the feature to manipulate in the what-if workflow, honoring explicit CLI selection when present.
     *
     * @param data          source dataset
     * @param options       what-if options
     * @param correlations  ranked feature correlations
     * @return selected feature, or {@code null} when no eligible zeroable feature exists
     */
    public WhatIfFeatureSelection select(Instances data,
                                         WhatIfOptions options,
                                         List<FeatureCorrelation> correlations) {
        if (options.getFeatureName() != null) {
            return explicitSelection(data, correlations, options.getFeatureName());
        }

        FeatureCorrelation preferred = findCorrelation(correlations, Config.DEFAULT_WHAT_IF_FEATURE);
        if (preferred != null && preferred.isZeroable()) {
            return new WhatIfFeatureSelection(
                    preferred,
                    "preferred exam feature '" + Config.DEFAULT_WHAT_IF_FEATURE + "'"
            );
        }

        for (FeatureCorrelation correlation : correlations) {
            if (correlation.isZeroable()) {
                return new WhatIfFeatureSelection(
                        correlation,
                        "highest absolute correlation among zeroable numeric features"
                );
            }
        }

        return null;
    }

    /**
     * Validates and returns the explicitly requested what-if feature.
     *
     * @param data         source dataset
     * @param correlations ranked feature correlations
     * @param featureName  requested feature name
     * @return selected feature information
     */
    private WhatIfFeatureSelection explicitSelection(Instances data,
                                                     List<FeatureCorrelation> correlations,
                                                     String featureName) {
        Attribute attribute = findAttribute(data, featureName);
        if (attribute == null) {
            throw new IllegalArgumentException("What-if feature '" + featureName + "' not found in dataset");
        }
        if (!attribute.isNumeric()) {
            throw new IllegalArgumentException("What-if feature '" + featureName + "' must be numeric");
        }

        FeatureCorrelation correlation = findCorrelation(correlations, featureName);
        if (correlation == null) {
            throw new IllegalArgumentException("No correlation data available for feature '" + featureName + "'");
        }
        if (!correlation.isZeroable()) {
            throw new IllegalArgumentException(
                    "What-if feature '" + featureName + "' must have both zero-valued and positive-valued instances"
            );
        }
        return new WhatIfFeatureSelection(correlation, "explicit CLI selection");
    }

    /**
     * Resolves a dataset attribute by name ignoring case.
     *
     * @param data        dataset to inspect
     * @param featureName attribute name to resolve
     * @return matching attribute, or {@code null} when absent
     */
    private Attribute findAttribute(Instances data, String featureName) {
        for (int index = 0; index < data.numAttributes(); index++) {
            Attribute attribute = data.attribute(index);
            if (attribute.name().equalsIgnoreCase(featureName)) {
                return attribute;
            }
        }
        return null;
    }

    /**
     * Resolves the correlation entry associated with the named feature ignoring case.
     *
     * @param correlations ranked feature correlations
     * @param featureName  feature name to resolve
     * @return matching correlation entry, or {@code null} when absent
     */
    private FeatureCorrelation findCorrelation(List<FeatureCorrelation> correlations, String featureName) {
        for (FeatureCorrelation correlation : correlations) {
            if (correlation.getFeatureName().equalsIgnoreCase(featureName)) {
                return correlation;
            }
        }
        return null;
    }
}

