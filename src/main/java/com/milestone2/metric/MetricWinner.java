package com.milestone2.metric;

import com.milestone2.classifier.ClassifierDefinition;

/**
 * Best classifier for one metric within a dataset analysis.
 */
public class MetricWinner {
    private final MetricDefinition metric;
    private final ClassifierDefinition classifierDefinition;
    private final double metricValue;

    public MetricWinner(MetricDefinition metric,
                        ClassifierDefinition classifierDefinition,
                        double metricValue) {
        this.metric = metric;
        this.classifierDefinition = classifierDefinition;
        this.metricValue = metricValue;
    }

    public MetricDefinition getMetric() {
        return metric;
    }

    public ClassifierDefinition getClassifierDefinition() {
        return classifierDefinition;
    }

    public double getMetricValue() {
        return metricValue;
    }
}

