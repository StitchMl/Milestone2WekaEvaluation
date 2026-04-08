package com.milestone2.feature;

import com.milestone2.evaluation.PositiveClassResolver;
import com.milestone2.analysis.AnalysisConfig;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Computes point-biserial style correlations for numeric features.
 */
public class FeatureCorrelationAnalyzer {
    private final PositiveClassResolver positiveClassResolver;
    private final PearsonCorrelationCalculator correlationCalculator;

    public FeatureCorrelationAnalyzer() {
        this(new PositiveClassResolver(), new PearsonCorrelationCalculator());
    }

    FeatureCorrelationAnalyzer(PositiveClassResolver positiveClassResolver,
                               PearsonCorrelationCalculator correlationCalculator) {
        this.positiveClassResolver = positiveClassResolver;
        this.correlationCalculator = correlationCalculator;
    }

    public List<FeatureCorrelation> analyze(Instances data, AnalysisConfig config) {
        int positiveClassIndex = positiveClassResolver.resolvePositiveClassIndex(data.classAttribute(), config);
        List<FeatureCorrelation> correlations = new ArrayList<>();
        for (int index = 0; index < data.numAttributes(); index++) {
            Attribute attribute = data.attribute(index);
            if (attribute.index() == data.classIndex() || !attribute.isNumeric()) {
                continue;
            }
            correlations.add(computeCorrelation(attribute, data, positiveClassIndex));
        }
        correlations.sort(Comparator
                .comparingDouble(FeatureCorrelation::getAbsoluteCorrelation)
                .reversed()
                .thenComparing(FeatureCorrelation::getFeatureName));
        return correlations;
    }

    private FeatureCorrelation computeCorrelation(Attribute attribute,
                                                  Instances data,
                                                  int positiveClassIndex) {
        int count = 0;
        int zeroValueCount = 0;
        int positiveValueCount = 0;
        double sumX = 0.0;
        double sumY = 0.0;
        double sumXX = 0.0;
        double sumYY = 0.0;
        double sumXY = 0.0;

        for (Instance instance : data) {
            if (instance.isMissing(attribute) || instance.classIsMissing()) {
                continue;
            }

            double featureValue = instance.value(attribute);
            double bugValue = instance.classValue() == positiveClassIndex ? 1.0 : 0.0;
            count++;
            if (Double.compare(featureValue, 0.0) == 0) {
                zeroValueCount++;
            }
            if (featureValue > 0.0) {
                positiveValueCount++;
            }

            sumX += featureValue;
            sumY += bugValue;
            sumXX += featureValue * featureValue;
            sumYY += bugValue * bugValue;
            sumXY += featureValue * bugValue;
        }

        return new FeatureCorrelation(
                attribute.name(),
                correlationCalculator.calculate(count, sumX, sumY, sumXX, sumYY, sumXY),
                count,
                zeroValueCount,
                positiveValueCount
        );
    }
}

