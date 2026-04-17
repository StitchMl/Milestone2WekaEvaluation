package com.milestone2.metric;

import com.milestone2.classifier.OverallClassifierWinner;
import com.milestone2.classifier.OverallClassifierWinnerSelector;
import com.milestone2.dataset.DatasetAnalysisReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * Logs the best classifier per metric for a dataset.
 */
public class BestMetricLogger {
    private static final Logger log = LoggerFactory.getLogger(BestMetricLogger.class);
    private final MetricWinnerSelector metricWinnerSelector = new MetricWinnerSelector();
    private final OverallClassifierWinnerSelector overallWinnerSelector = new OverallClassifierWinnerSelector();

    /**
     * Logs the best classifier for each metric and the overall milestone winner for the dataset.
     *
     * @param report dataset analysis report
     */
    public void log(DatasetAnalysisReport report) {
        if (report.getClassifierReports().isEmpty()) {
            return;
        }

        for (MetricWinner winner : metricWinnerSelector.select(report)) {
            log.info("[{}] Best classifier for {}: {} ({})",
                    report.getDatasetName(),
                    winner.getMetric().getDisplayName(),
                    winner.getClassifierDefinition().getDisplayName(),
                    String.format(Locale.US, "%.4f", winner.getMetricValue()));
        }

        OverallClassifierWinner overallWinner = overallWinnerSelector.select(report);
        if (overallWinner != null) {
            log.info("[{}] Overall milestone winner: {} (Kappa={}, AUC={})",
                    report.getDatasetName(),
                    overallWinner.getClassifierDefinition().getDisplayName(),
                    String.format(Locale.US, "%.4f", overallWinner.getKappa()),
                    String.format(Locale.US, "%.4f", overallWinner.getAuc()));
        }
    }
}

