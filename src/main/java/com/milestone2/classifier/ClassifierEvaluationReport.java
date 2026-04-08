package com.milestone2.classifier;

import com.milestone2.metric.MetricDefinition;
import com.milestone2.fold.PerFoldResult;

import java.util.Collections;
import java.util.EnumMap;
import java.util.ArrayList;
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
        this.foldResults = Collections.unmodifiableList(new ArrayList<>(foldResults));
    }

    public ClassifierDefinition getDefinition() {
        return definition;
    }

    public Map<MetricDefinition, Double> getAggregateMetrics() {
        return aggregateMetrics;
    }

    public List<PerFoldResult> getFoldResults() {
        return foldResults;
    }

    private Map<MetricDefinition, Double> immutableMetricMap(Map<MetricDefinition, Double> metrics) {
        Map<MetricDefinition, Double> copy = new EnumMap<>(MetricDefinition.class);
        copy.putAll(metrics);
        return Collections.unmodifiableMap(copy);
    }
}

