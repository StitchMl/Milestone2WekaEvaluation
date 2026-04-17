package com.milestone2.summary;

import com.milestone2.classifier.OverallClassifierWinner;
import com.milestone2.analysis.AnalysisConfig;
import com.milestone2.dataset.DatasetAnalysisReport;
import com.milestone2.metric.MetricWinner;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds CSV rows for the milestone summary export.
 */
public class Milestone2SummaryRecordFactory {
    /**
     * Builds one CSV record describing the winner for a specific metric.
     *
     * @param config immutable analysis configuration
     * @param report dataset analysis report
     * @param winner metric winner to serialize
     * @return CSV record values
     */
    public List<Object> metricWinnerRecord(AnalysisConfig config,
                                           DatasetAnalysisReport report,
                                           MetricWinner winner) {
        List<Object> record = baseRecord(config, report);
        record.add("METRIC_WINNER");
        record.add(winner.getMetric().getDisplayName());
        record.add(winner.getClassifierDefinition().getDisplayName());
        record.add(winner.getClassifierDefinition().getId());
        record.add(winner.getClassifierDefinition().getClassName());
        record.add(winner.getMetricValue());
        record.add(null);
        record.add(null);
        record.add("best classifier for metric " + winner.getMetric().getDisplayName());
        return record;
    }

    /**
     * Builds one CSV record describing the overall milestone winner.
     *
     * @param config immutable analysis configuration
     * @param report dataset analysis report
     * @param winner overall classifier winner
     * @return CSV record values
     */
    public List<Object> overallWinnerRecord(AnalysisConfig config,
                                            DatasetAnalysisReport report,
                                            OverallClassifierWinner winner) {
        List<Object> record = baseRecord(config, report);
        record.add("OVERALL_WINNER");
        record.add("Kappa/AUC");
        record.add(winner.getClassifierDefinition().getDisplayName());
        record.add(winner.getClassifierDefinition().getId());
        record.add(winner.getClassifierDefinition().getClassName());
        record.add(null);
        record.add(winner.getKappa());
        record.add(winner.getAuc());
        record.add(winner.getReason());
        return record;
    }

    /**
     * Builds the metadata prefix shared by every milestone summary CSV row.
     *
     * @param config immutable analysis configuration
     * @param report dataset analysis report
     * @return base CSV record values
     */
    private List<Object> baseRecord(AnalysisConfig config, DatasetAnalysisReport report) {
        List<Object> record = new ArrayList<>();
        record.add(config.getExecution().getRunId());
        record.add(config.getSelection().getGranularity());
        record.add(report.getDatasetName());
        record.add(config.getExecution().getValidationStrategy().getCliValue());
        record.add(config.getExecution().getTemporalAttributeName());
        record.add(report.getClassAttributeName());
        record.add(report.getPositiveClassValue());
        return record;
    }
}

