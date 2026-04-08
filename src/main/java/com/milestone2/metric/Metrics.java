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

    public double get(MetricDefinition metric) {
        return values.get(metric);
    }

    @Override
    public String toString() {
        return "Metrics{" + "values=" + values + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Metrics metrics = (Metrics) o;
        return Objects.equals(values, metrics.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }
}

