package com.milestone2.metric;

import com.milestone2.fold.PerFoldResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Aggregates fold metrics into stable ordered averages.
 */
public class MetricAggregator {
    private static final Logger log = LoggerFactory.getLogger(MetricAggregator.class);

    /**
     * Computes the average value of every supported metric across the provided fold results.
     *
     * @param results fold-level evaluation results
     * @return aggregate metrics map
     */
    public Map<MetricDefinition, Double> aggregate(List<PerFoldResult> results) {
        Map<MetricDefinition, Double> sums = initializeNumericMap();
        Map<MetricDefinition, Integer> counts = initializeCounterMap();

        for (PerFoldResult result : results) {
            Metrics metrics = result.getMetrics();
            for (MetricDefinition metric : MetricDefinition.values()) {
                addMetricValue(sums, counts, metric, metric.extract(metrics));
            }
        }

        Map<MetricDefinition, Double> averages = new EnumMap<>(MetricDefinition.class);
        for (MetricDefinition metric : MetricDefinition.values()) {
            int count = counts.get(metric);
            averages.put(metric, count == 0 ? Double.NaN : sums.get(metric) / count);
        }

        log.debug("Aggregated metrics: {}", averages);
        return averages;
    }

    /**
     * Initializes a numeric accumulator map with one zero entry per supported metric.
     *
     * @return metric sum accumulator
     */
    private Map<MetricDefinition, Double> initializeNumericMap() {
        Map<MetricDefinition, Double> sums = new EnumMap<>(MetricDefinition.class);
        for (MetricDefinition metric : MetricDefinition.values()) {
            sums.put(metric, 0.0);
        }
        return sums;
    }

    /**
     * Initializes a counter map with one zero entry per supported metric.
     *
     * @return metric counter accumulator
     */
    private Map<MetricDefinition, Integer> initializeCounterMap() {
        Map<MetricDefinition, Integer> counts = new EnumMap<>(MetricDefinition.class);
        for (MetricDefinition metric : MetricDefinition.values()) {
            counts.put(metric, 0);
        }
        return counts;
    }

    /**
     * Adds one metric value to the accumulators, ignoring missing values represented as {@link Double#NaN}.
     *
     * @param sums    metric sum accumulator
     * @param counts  metric counter accumulator
     * @param metric  metric being updated
     * @param value   metric value to add
     */
    private void addMetricValue(Map<MetricDefinition, Double> sums,
                                Map<MetricDefinition, Integer> counts,
                                MetricDefinition metric,
                                double value) {
        if (Double.isNaN(value)) {
            return;
        }
        sums.put(metric, sums.get(metric) + value);
        counts.put(metric, counts.get(metric) + 1);
    }
}

