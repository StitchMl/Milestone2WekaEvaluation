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

    /**
     * Returns the metric for which the classifier won.
     *
     * @return winning metric
     */
    public MetricDefinition getMetric() {
        return metric;
    }

    /**
     * Returns the classifier that achieved the best value for the metric.
     *
     * @return winning classifier definition
     */
    public ClassifierDefinition getClassifierDefinition() {
        return classifierDefinition;
    }

    /**
     * Returns the winning aggregate value for the metric.
     *
     * @return winning metric value
     */
    public double getMetricValue() {
        return metricValue;
    }
}

