package com.milestone2;

import com.milestone2.analysis.AnalysisConfig;
import com.milestone2.feature.FeatureCorrelation;
import com.milestone2.whatif.WhatIfFeatureSelection;
import com.milestone2.whatif.WhatIfFeatureSelector;
import org.junit.jupiter.api.Test;
import weka.core.Instances;

import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WhatIfFeatureSelectorTest {

    @Test
    void selectPrefersNSmellsWhenItIsAvailableAndZeroable() throws Exception {
        Instances data = dataset(
                "@relation demo%n" +
                        "@attribute Complexity numeric%n" +
                        "@attribute NSmells numeric%n" +
                        "@attribute bug {yes,no}%n" +
                        "@data%n" +
                        "1,0,no%n" +
                        "2,2,yes%n" +
                        "3,0,no%n" +
                        "4,1,yes%n"
        );

        List<FeatureCorrelation> correlations = List.of(
                new FeatureCorrelation("Complexity", 0.99, 4, 0, 4),
                new FeatureCorrelation("NSmells", 0.50, 4, 2, 2)
        );

        WhatIfFeatureSelection selection = new WhatIfFeatureSelector().select(
                data,
                AnalysisConfig.fromArgs(new String[0]).getWhatIfOptions(),
                correlations
        );

        assertEquals("NSmells", selection.getFeatureName());
    }

    @SuppressWarnings("SameParameterValue")
    private Instances dataset(String arff) throws Exception {
        Instances data = new Instances(new StringReader(String.format(arff)));
        data.setClassIndex(data.numAttributes() - 1);
        return data;
    }
}

