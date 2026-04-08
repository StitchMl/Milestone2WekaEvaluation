package com.milestone2.whatif;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Builds the A, B+, B and C datasets required by the exam workflow.
 */
public class WhatIfDatasetBuilder {
    public WhatIfDatasetSet build(Instances source, WhatIfFeatureSelection featureSelection) {
        Attribute feature = source.attribute(featureSelection.getFeatureName());
        Instances originalDataset = new Instances(source);
        Instances bPlusDataset = subset(source, feature, true);
        Instances cDataset = subset(source, feature, false);
        Instances bDataset = zeroedCopy(bPlusDataset, feature.name());
        return new WhatIfDatasetSet(originalDataset, bPlusDataset, bDataset, cDataset);
    }

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

    private Instances zeroedCopy(Instances source, String featureName) {
        Instances copy = new Instances(source);
        Attribute feature = copy.attribute(featureName);
        for (Instance instance : copy) {
            instance.setValue(feature, 0.0);
        }
        return copy;
    }
}

