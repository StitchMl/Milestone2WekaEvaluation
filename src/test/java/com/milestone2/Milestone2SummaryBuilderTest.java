package com.milestone2;

import com.milestone2.classifier.ClassifierDefinition;
import com.milestone2.classifier.ClassifierEvaluationReport;
import com.milestone2.dataset.DatasetAnalysisReport;
import com.milestone2.metric.MetricDefinition;
import com.milestone2.metric.MetricWinner;
import com.milestone2.summary.Milestone2Summary;
import com.milestone2.summary.Milestone2SummaryBuilder;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Milestone2SummaryBuilderTest {

    @Test
    void buildSelectsPerMetricWinnersAndOverallWinner() {
        DatasetAnalysisReport report = new DatasetAnalysisReport(
                "demo.csv",
                "bug",
                "yes",
                List.of(
                        classifierReport("RF", "Random Forest", 0.30, 0.91, 0.82),
                        classifierReport("NB", "Naive Bayes", 0.42, 0.70, 0.75)
                )
        );

        Milestone2Summary summary = new Milestone2SummaryBuilder().build(report);

        assertEquals(MetricDefinition.values().length, summary.getMetricWinners().size());
        assertEquals("RF", findMetricWinner(summary, MetricDefinition.ACCURACY).getClassifierDefinition().getId());
        assertEquals("NB", summary.getOverallWinner().getClassifierDefinition().getId());
        assertEquals(0.42, summary.getOverallWinner().getKappa());
        assertEquals(0.70, summary.getOverallWinner().getAuc());
    }

    @SuppressWarnings("SameParameterValue")
    private MetricWinner findMetricWinner(Milestone2Summary summary, MetricDefinition metric) {
        return summary.getMetricWinners().stream()
                .filter(winner -> winner.getMetric() == metric)
                .findFirst()
                .orElseThrow();
    }

    private ClassifierEvaluationReport classifierReport(String id,
                                                        String displayName,
                                                        double kappa,
                                                        double auc,
                                                        double accuracy) {
        Map<MetricDefinition, Double> metrics = new EnumMap<>(MetricDefinition.class);
        for (MetricDefinition metric : MetricDefinition.values()) {
            metrics.put(metric, 0.50);
        }
        metrics.put(MetricDefinition.KAPPA, kappa);
        metrics.put(MetricDefinition.AUC, auc);
        metrics.put(MetricDefinition.ACCURACY, accuracy);
        return new ClassifierEvaluationReport(
                new ClassifierDefinition(id, displayName, "weka.classifiers.Dummy", ""),
                metrics,
                List.of()
        );
    }
}

