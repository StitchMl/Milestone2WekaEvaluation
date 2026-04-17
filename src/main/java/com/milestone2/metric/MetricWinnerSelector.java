package com.milestone2.metric;

import com.milestone2.dataset.DatasetAnalysisReport;
import com.milestone2.classifier.ClassifierDefinition;
import com.milestone2.classifier.ClassifierEvaluationReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Selects the best classifier for each supported metric.
 */
public class MetricWinnerSelector {
    /**
     * Selects one winning classifier for every supported metric in the dataset report.
     *
     * @param report dataset analysis report
     * @return metric winners in metric declaration order
     */
    public List<MetricWinner> select(DatasetAnalysisReport report) {
        List<MetricWinner> winners = new ArrayList<>();
        for (MetricDefinition metric : MetricDefinition.values()) {
            MetricWinner winner = select(metric, report);
            if (winner != null) {
                winners.add(winner);
            }
        }
        return winners;
    }

    /**
     * Selects the classifier with the best aggregate value for the requested metric.
     *
     * @param metric metric to maximize
     * @param report dataset analysis report
     * @return metric winner, or {@code null} when no valid value is available
     */
    public MetricWinner select(MetricDefinition metric, DatasetAnalysisReport report) {
        ClassifierDefinition bestClassifier = null;
        double bestValue = Double.NEGATIVE_INFINITY;

        for (ClassifierEvaluationReport classifierReport : report.getClassifierReports()) {
            Double currentValue = classifierReport.getAggregateMetrics().get(metric);
            if (currentValue != null && !Double.isNaN(currentValue) && currentValue > bestValue) {
                bestValue = currentValue;
                bestClassifier = classifierReport.getDefinition();
            }
        }

        return bestClassifier == null ? null : new MetricWinner(metric, bestClassifier, bestValue);
    }
}

