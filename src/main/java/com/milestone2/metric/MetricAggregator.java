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

    private Map<MetricDefinition, Double> initializeNumericMap() {
        Map<MetricDefinition, Double> sums = new EnumMap<>(MetricDefinition.class);
        for (MetricDefinition metric : MetricDefinition.values()) {
            sums.put(metric, 0.0);
        }
        return sums;
    }

    private Map<MetricDefinition, Integer> initializeCounterMap() {
        Map<MetricDefinition, Integer> counts = new EnumMap<>(MetricDefinition.class);
        for (MetricDefinition metric : MetricDefinition.values()) {
            counts.put(metric, 0);
        }
        return counts;
    }

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

