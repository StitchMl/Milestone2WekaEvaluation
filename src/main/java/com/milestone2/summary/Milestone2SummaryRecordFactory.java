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

    private List<Object> baseRecord(AnalysisConfig config, DatasetAnalysisReport report) {
        List<Object> record = new ArrayList<>();
        record.add(config.getExecution().getRunId());
        record.add(config.getSelection().getGranularity());
        record.add(report.getDatasetName());
        record.add(report.getClassAttributeName());
        record.add(report.getPositiveClassValue());
        return record;
    }
}

