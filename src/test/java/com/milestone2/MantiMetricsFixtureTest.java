package com.milestone2;

import com.milestone2.analysis.AnalysisConfig;
import com.milestone2.dataset.GenericDataLoader;
import com.milestone2.feature.FeatureCorrelation;
import com.milestone2.whatif.WhatIfDatasetBuilder;
import com.milestone2.whatif.WhatIfDatasetSet;
import com.milestone2.whatif.WhatIfFeatureSelection;
import org.junit.jupiter.api.Test;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MantiMetricsFixtureTest {
    private static final Path MAIN_DATASET =
            Paths.get("src/main/resources/data/avro_dataset_class.csv");
    private static final Path ARTIFACTS_DIR =
            Paths.get("src/test/resources/mantimetrics/avro_dataset_class_artifacts");

    @Test
    void realAvroDatasetLoadsWithExpectedClassAttribute() throws Exception {
        AnalysisConfig config = AnalysisConfig.fromArgs(new String[]{
                "--class-attribute=Buggy",
                "--positive-class=yes"
        });

        Instances data = new GenericDataLoader().load(MAIN_DATASET, config);

        assertEquals("Buggy", data.classAttribute().name());
        assertEquals(1073, data.numInstances());
    }

    @Test
    void whatIfBuilderMatchesImportedAvroArtifactPartitionsForNSmells() throws Exception {
        AnalysisConfig config = AnalysisConfig.fromArgs(new String[]{"--class-attribute=Buggy"});
        GenericDataLoader loader = new GenericDataLoader();
        Instances aDataset = loader.load(ARTIFACTS_DIR.resolve("A.csv"), config);
        Instances expectedBPlus = loader.load(ARTIFACTS_DIR.resolve("BPlus.csv"), config);
        Instances expectedB = loader.load(ARTIFACTS_DIR.resolve("B.csv"), config);
        Instances expectedC = loader.load(ARTIFACTS_DIR.resolve("C.csv"), config);

        WhatIfDatasetSet actual = new WhatIfDatasetBuilder().build(aDataset, selectionFor(aDataset, "NSmells"));

        assertEquals(aDataset.numInstances(), actual.getOriginalDataset().numInstances());
        assertEquals(expectedBPlus.numInstances(), actual.getBPlusDataset().numInstances());
        assertEquals(expectedB.numInstances(), actual.getBDataset().numInstances());
        assertEquals(expectedC.numInstances(), actual.getCDataset().numInstances());
        assertAllPositive(actual.getBPlusDataset(), "NSmells");
        assertAllZero(actual.getBDataset(), "NSmells");
        assertAllZero(actual.getCDataset(), "NSmells");
    }

    @SuppressWarnings("SameParameterValue")
    private WhatIfFeatureSelection selectionFor(Instances data, String featureName) {
        Attribute feature = data.attribute(featureName);
        int nonMissingCount = 0;
        int zeroValueCount = 0;
        int positiveValueCount = 0;

        for (Instance instance : data) {
            if (instance.isMissing(feature)) {
                continue;
            }

            nonMissingCount++;
            double value = instance.value(feature);
            if (Double.compare(value, 0.0) == 0) {
                zeroValueCount++;
            }
            if (value > 0.0) {
                positiveValueCount++;
            }
        }

        return new WhatIfFeatureSelection(
                new FeatureCorrelation(featureName, 0.0, nonMissingCount, zeroValueCount, positiveValueCount),
                "Imported fixture"
        );
    }

    @SuppressWarnings("SameParameterValue")
    private void assertAllPositive(Instances data, String featureName) {
        Attribute feature = data.attribute(featureName);
        for (Instance instance : data) {
            assertTrue(instance.value(feature) > 0.0);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void assertAllZero(Instances data, String featureName) {
        Attribute feature = data.attribute(featureName);
        for (Instance instance : data) {
            assertEquals(0.0, instance.value(feature));
        }
    }
}

