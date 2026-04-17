package com.milestone2.classifier;

import com.milestone2.dataset.DatasetAnalysisReport;
import com.milestone2.metric.MetricDefinition;

import java.util.Comparator;

/**
 * Selects the milestone winner using Kappa first and AUC as tie-breaker.
 */
public class OverallClassifierWinnerSelector {
    /**
     * Selects the best overall classifier for the dataset according to the milestone ranking rule.
     *
     * @param report dataset analysis report
     * @return overall classifier winner, or {@code null} when no classifier reports are available
     */
    public OverallClassifierWinner select(DatasetAnalysisReport report) {
        ClassifierEvaluationReport bestReport = report.getClassifierReports().stream()
                .max(Comparator
                        .comparingDouble((ClassifierEvaluationReport classifierReport) ->
                                metric(classifierReport, MetricDefinition.KAPPA))
                        .thenComparingDouble(classifierReport ->
                                metric(classifierReport, MetricDefinition.AUC))
                        .thenComparing(classifierReport ->
                                classifierReport.getDefinition().getDisplayName()))
                .orElse(null);

        if (bestReport == null) {
            return null;
        }

        return new OverallClassifierWinner(
                bestReport.getDefinition(),
                metric(bestReport, MetricDefinition.KAPPA),
                metric(bestReport, MetricDefinition.AUC),
                "best overall classifier by Kappa, then AUC"
        );
    }

    /**
     * Reads one aggregate metric from the classifier report.
     *
     * @param classifierReport classifier report
     * @param metric           metric to extract
     * @return aggregate metric value
     */
    private double metric(ClassifierEvaluationReport classifierReport, MetricDefinition metric) {
        return classifierReport.getAggregateMetrics().get(metric);
    }
}

