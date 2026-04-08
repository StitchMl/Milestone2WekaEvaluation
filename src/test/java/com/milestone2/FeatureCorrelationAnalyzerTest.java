package com.milestone2;

import com.milestone2.analysis.AnalysisConfig;
import com.milestone2.feature.FeatureCorrelation;
import com.milestone2.feature.FeatureCorrelationAnalyzer;
import org.junit.jupiter.api.Test;
import weka.core.Instances;

import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureCorrelationAnalyzerTest {

    @Test
    void analyzeRanksNumericFeaturesByAbsoluteCorrelation() throws Exception {
        Instances data = dataset(
                "@relation demo%n" +
                        "@attribute NSmells numeric%n" +
                        "@attribute LOC numeric%n" +
                        "@attribute bug {yes,no}%n" +
                        "@data%n" +
                        "0,10,no%n" +
                        "2,10,yes%n" +
                        "0,10,no%n" +
                        "3,10,yes%n"
        );

        List<FeatureCorrelation> correlations = new FeatureCorrelationAnalyzer()
                .analyze(data, AnalysisConfig.fromArgs(new String[]{"--positive-class=yes"}));

        assertEquals(2, correlations.size());
        assertEquals("NSmells", correlations.get(0).getFeatureName());
        assertEquals(2, correlations.get(0).getZeroValueCount());
        assertEquals(2, correlations.get(0).getPositiveValueCount());
        assertTrue(correlations.get(0).isZeroable());
        assertEquals(0.0, correlations.get(1).getAbsoluteCorrelation());
    }

    @SuppressWarnings("SameParameterValue")
    private Instances dataset(String arff) throws Exception {
        Instances data = new Instances(new StringReader(String.format(arff)));
        data.setClassIndex(data.numAttributes() - 1);
        return data;
    }
}

