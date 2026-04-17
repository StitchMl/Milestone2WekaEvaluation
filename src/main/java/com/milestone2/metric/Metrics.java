package com.milestone2.metric;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable metric values for one evaluation result.
 */
public class Metrics {
    private final Map<MetricDefinition, Double> values;

    public Metrics(double accuracy,
                   double precision,
                   double recall,
                   double f1,
                   double kappa,
                   double auc,
                   double npOfb20) {
        EnumMap<MetricDefinition, Double> map = new EnumMap<>(MetricDefinition.class);
        map.put(MetricDefinition.ACCURACY, accuracy);
        map.put(MetricDefinition.PRECISION, precision);
        map.put(MetricDefinition.RECALL, recall);
        map.put(MetricDefinition.F1, f1);
        map.put(MetricDefinition.KAPPA, kappa);
        map.put(MetricDefinition.AUC, auc);
        map.put(MetricDefinition.NPOFB20, npOfb20);
        values = Collections.unmodifiableMap(map);
    }

    /**
     * Returns the stored value for the requested metric.
     *
     * @param metric metric to read
     * @return metric value
     */
    public double get(MetricDefinition metric) {
        return values.get(metric);
    }

    /**
     * Returns a debug-friendly textual representation of the metric bundle.
     *
     * @return string representation of the metrics
     */
    @Override
    public String toString() {
        return "Metrics{" + "values=" + values + '}';
    }

    /**
     * Compares this metric bundle with another object using the stored metric map.
     *
     * @param o object to compare with
     * @return {@code true} when both objects contain the same metric values
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Metrics metrics = (Metrics) o;
        return Objects.equals(values, metrics.values);
    }

    /**
     * Computes the hash code consistent with {@link #equals(Object)}.
     *
     * @return metrics hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(values);
    }
}

