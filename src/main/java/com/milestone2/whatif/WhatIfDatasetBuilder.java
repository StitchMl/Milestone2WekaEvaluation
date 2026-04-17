package com.milestone2.whatif;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Builds the A, B+, B and C datasets required by the exam workflow.
 */
public class WhatIfDatasetBuilder {
    /**
     * Builds the derived datasets used by the what-if workflow starting from the selected feature.
     *
     * @param source           original dataset
     * @param featureSelection selected feature information
     * @return derived what-if dataset set
     */
    public WhatIfDatasetSet build(Instances source, WhatIfFeatureSelection featureSelection) {
        Attribute feature = source.attribute(featureSelection.getFeatureName());
        Instances originalDataset = new Instances(source);
        Instances bPlusDataset = subset(source, feature, true);
        Instances cDataset = subset(source, feature, false);
        Instances bDataset = zeroedCopy(bPlusDataset, feature.name());
        return new WhatIfDatasetSet(originalDataset, bPlusDataset, bDataset, cDataset);
    }

    /**
     * Extracts the subset of instances whose selected feature is either strictly positive or exactly zero.
     *
     * @param source            source dataset
     * @param feature           selected feature
     * @param strictlyPositive  {@code true} for B+, {@code false} for C
     * @return derived subset
     */
    private Instances subset(Instances source, Attribute feature, boolean strictlyPositive) {
        Instances subset = new Instances(source, 0);
        for (Instance instance : source) {
            if (instance.isMissing(feature)) {
                continue;
            }

            double value = instance.value(feature);
            boolean matches = strictlyPositive ? value > 0.0 : Double.compare(value, 0.0) == 0;
            if (matches) {
                subset.add((Instance) instance.copy());
            }
        }
        return subset;
    }

    /**
     * Creates a copy of the dataset where the selected feature is forced to zero in every instance.
     *
     * @param source      source dataset
     * @param featureName feature to zero out
     * @return zeroed dataset copy
     */
    private Instances zeroedCopy(Instances source, String featureName) {
        Instances copy = new Instances(source);
        Attribute feature = copy.attribute(featureName);
        for (Instance instance : copy) {
            instance.setValue(feature, 0.0);
        }
        return copy;
    }
}

