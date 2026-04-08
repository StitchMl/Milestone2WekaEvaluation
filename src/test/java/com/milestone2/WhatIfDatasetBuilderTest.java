package com.milestone2;

import com.milestone2.feature.FeatureCorrelation;
import com.milestone2.whatif.WhatIfDatasetBuilder;
import com.milestone2.whatif.WhatIfDatasetSet;
import com.milestone2.whatif.WhatIfFeatureSelection;
import org.junit.jupiter.api.Test;
import weka.core.Instances;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WhatIfDatasetBuilderTest {

    @Test
    void buildCreatesABPlusBAndCFromSelectedFeature() throws Exception {
        Instances data = dataset(
                "@relation demo%n" +
                        "@attribute NSmells numeric%n" +
                        "@attribute LOC numeric%n" +
                        "@attribute bug {yes,no}%n" +
                        "@data%n" +
                        "0,10,no%n" +
                        "2,15,yes%n" +
                        "0,11,no%n" +
                        "3,17,yes%n"
        );

        WhatIfDatasetSet datasets = new WhatIfDatasetBuilder().build(
                data,
                new WhatIfFeatureSelection(
                        new FeatureCorrelation("NSmells", 1.0, 4, 2, 2),
                        "test"
                )
        );

        assertEquals(4, datasets.getOriginalDataset().numInstances());
        assertEquals(2, datasets.getBPlusDataset().numInstances());
        assertEquals(2, datasets.getBDataset().numInstances());
        assertEquals(2, datasets.getCDataset().numInstances());
        assertEquals(2.0, datasets.getBPlusDataset().instance(0).value(0));
        assertEquals(3.0, datasets.getBPlusDataset().instance(1).value(0));
        assertEquals(0.0, datasets.getBDataset().instance(0).value(0));
        assertEquals(0.0, datasets.getBDataset().instance(1).value(0));
    }

    @SuppressWarnings("SameParameterValue")
    private Instances dataset(String arff) throws Exception {
        Instances data = new Instances(new StringReader(String.format(arff)));
        data.setClassIndex(data.numAttributes() - 1);
        return data;
    }
}

