package com.milestone2;
import com.milestone2.fold.PerFoldResult;

import com.milestone2.metric.MetricAggregator;
import com.milestone2.metric.MetricDefinition;
import com.milestone2.metric.Metrics;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModelEvaluatorTest {
    private final MetricAggregator metricAggregator = new MetricAggregator();

    @Test
    void aggregateComputesExpectedAveragesInStableOrder() {
        List<PerFoldResult> results = List.of(
                new PerFoldResult(0, 0, new Metrics(90.0, 0.80, 0.70, 0.75, 0.60, 0.85, 0.50)),
                new PerFoldResult(0, 1, new Metrics(70.0, 0.60, 0.50, 0.55, 0.40, 0.65, 0.30))
        );

        Map<MetricDefinition, Double> avg = metricAggregator.aggregate(results);

        assertEquals(
                List.of(
                        MetricDefinition.ACCURACY,
                        MetricDefinition.PRECISION,
                        MetricDefinition.RECALL,
                        MetricDefinition.F1,
                        MetricDefinition.KAPPA,
                        MetricDefinition.AUC,
                        MetricDefinition.NPOFB20
                ),
                new ArrayList<>(avg.keySet())
        );
        assertEquals(80.0, avg.get(MetricDefinition.ACCURACY));
        assertEquals(0.70, avg.get(MetricDefinition.PRECISION));
        assertEquals(0.60, avg.get(MetricDefinition.RECALL));
        assertEquals(0.65, avg.get(MetricDefinition.F1));
        assertEquals(0.50, avg.get(MetricDefinition.KAPPA));
        assertEquals(0.75, avg.get(MetricDefinition.AUC));
        assertEquals(0.40, avg.get(MetricDefinition.NPOFB20));
    }
}

