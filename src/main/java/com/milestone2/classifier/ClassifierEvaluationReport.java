package com.milestone2.classifier;

import com.milestone2.metric.MetricDefinition;
import com.milestone2.fold.PerFoldResult;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Holds the fold-level and aggregate outcome for one classifier on one dataset.
 */
public class ClassifierEvaluationReport {
    private final ClassifierDefinition definition;
    private final Map<MetricDefinition, Double> aggregateMetrics;
    private final List<PerFoldResult> foldResults;

    public ClassifierEvaluationReport(ClassifierDefinition definition,
                                      Map<MetricDefinition, Double> aggregateMetrics,
                                      List<PerFoldResult> foldResults) {
        this.definition = definition;
        this.aggregateMetrics = immutableMetricMap(aggregateMetrics);
        this.foldResults = List.copyOf(foldResults);
    }

    /**
     * Returns the classifier definition that produced this report.
     *
     * @return classifier definition
     */
    public ClassifierDefinition getDefinition() {
        return definition;
    }

    /**
     * Returns the aggregate metrics computed across all evaluated folds.
     *
     * @return immutable aggregate metric map
     */
    public Map<MetricDefinition, Double> getAggregateMetrics() {
        return aggregateMetrics;
    }

    /**
     * Returns the fold-level results used to compute the aggregate metrics.
     *
     * @return immutable per-fold results list
     */
    public List<PerFoldResult> getFoldResults() {
        return foldResults;
    }

    /**
     * Copies the metric map into an immutable enum map to preserve ordering and defensive immutability.
     *
     * @param metrics source metric map
     * @return immutable metric map copy
     */
    private Map<MetricDefinition, Double> immutableMetricMap(Map<MetricDefinition, Double> metrics) {
        Map<MetricDefinition, Double> copy = new EnumMap<>(MetricDefinition.class);
        copy.putAll(metrics);
        return Collections.unmodifiableMap(copy);
    }
}

