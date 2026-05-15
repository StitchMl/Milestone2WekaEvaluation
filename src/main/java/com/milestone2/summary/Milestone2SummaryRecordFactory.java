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
 List<Object> row = baseRecord(config, report);
 row.add("METRIC_WINNER");
 row.add(winner.getMetric().getDisplayName());
 row.add(winner.getClassifierDefinition().getDisplayName());
 row.add(winner.getClassifierDefinition().getId());
 row.add(winner.getClassifierDefinition().getClassName());
 row.add(winner.getMetricValue());
 row.add(null);
 row.add(null);
 row.add("best classifier for metric " + winner.getMetric().getDisplayName());
 return row;
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
 List<Object> row = baseRecord(config, report);
 row.add("OVERALL_WINNER");
 row.add("Kappa/AUC");
 row.add(winner.getClassifierDefinition().getDisplayName());
 row.add(winner.getClassifierDefinition().getId());
 row.add(winner.getClassifierDefinition().getClassName());
 row.add(null);
 row.add(winner.getKappa());
 row.add(winner.getAuc());
 row.add(winner.getReason());
 return row;
 }

 /**
 * Builds the metadata prefix shared by every milestone summary CSV row.
 *
 * @param config immutable analysis configuration
 * @param report dataset analysis report
 * @return base CSV record values
 */
 private List<Object> baseRecord(AnalysisConfig config, DatasetAnalysisReport report) {
 List<Object> row = new ArrayList<>();
 row.add(config.getExecution().getRunId());
 row.add(config.getSelection().getGranularity());
 row.add(report.getDatasetName());
 row.add(config.getExecution().getValidationStrategy().getCliValue());
 row.add(config.getExecution().getTemporalAttributeName());
 row.add(report.getClassAttributeName());
 row.add(report.getPositiveClassValue());
 return row;
 }
}

