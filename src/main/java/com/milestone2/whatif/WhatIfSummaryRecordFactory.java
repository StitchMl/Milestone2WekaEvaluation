package com.milestone2.whatif;

import com.milestone2.prediction.ScenarioPredictionSummary;
import com.milestone2.analysis.AnalysisConfig;
import com.milestone2.dataset.DatasetAnalysisReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds CSV rows for the what-if summary writer.
 */
public class WhatIfSummaryRecordFactory {
 /**
 * Builds one CSV record describing a scenario summary row.
 *
 * @param config immutable analysis configuration
 * @param report dataset analysis report
 * @param scenarioReport what-if scenario report
 * @param summary scenario summary to serialize
 * @return CSV record values
 */
 public List<Object> buildScenarioRecord(AnalysisConfig config,
 DatasetAnalysisReport report,
 WhatIfScenarioReport scenarioReport,
 ScenarioPredictionSummary summary) {
 List<Object> row = commonRecord(config, report, scenarioReport);
 row.add("SCENARIO");
 row.add(summary.getScenario().getDisplayName());
 row.add(summary.getInstanceCount());
 row.add(summary.getActualBuggyCount());
 row.add(summary.getPredictedBuggyCount());
 row.add(summary.getAveragePositiveProbability());
 row.add(null);
 row.add(null);
 row.add(null);
 row.add(null);
 return row;
 }

 /**
 * Builds one CSV record describing the paired B+/B impact summary.
 *
 * @param config immutable analysis configuration
 * @param report dataset analysis report
 * @param scenarioReport what-if scenario report
 * @return CSV record values
 */
 public List<Object> buildImpactRecord(AnalysisConfig config,
 DatasetAnalysisReport report,
 WhatIfScenarioReport scenarioReport) {
 WhatIfImpactSummary impact = scenarioReport.getImpactSummary();
 List<Object> row = commonRecord(config, report, scenarioReport);
 row.add("IMPACT");
 row.add("B+->B");
 row.add(impact.getPairedInstanceCount());
 row.add(impact.getActualBuggyCount());
 row.add(null);
 row.add(null);
 row.add(impact.getPredictedRelievedCount());
 row.add(impact.getAvoidableBuggyCount());
 row.add(impact.getAvoidableBuggyShare());
 row.add(impact.getAveragePositiveProbabilityReduction());
 return row;
 }

 /**
 * Builds the metadata prefix shared by every what-if summary CSV row.
 *
 * @param config immutable analysis configuration
 * @param report dataset analysis report
 * @param scenarioReport what-if scenario report
 * @return base CSV record values
 */
 private List<Object> commonRecord(AnalysisConfig config,
 DatasetAnalysisReport report,
 WhatIfScenarioReport scenarioReport) {
 List<Object> row = new ArrayList<>();
 row.add(config.getExecution().getRunId());
 row.add(config.getSelection().getGranularity());
 row.add(report.getDatasetName());
 row.add(config.getExecution().getValidationStrategy().getCliValue());
 row.add(config.getExecution().getTemporalAttributeName());
 row.add(report.getClassAttributeName());
 row.add(report.getPositiveClassValue());
 row.add(scenarioReport.getFeatureSelection().getFeatureName());
 row.add(scenarioReport.getFeatureSelection().getReason());
 row.add(scenarioReport.getClassifierSelection().getDefinition().getDisplayName());
 row.add(scenarioReport.getClassifierSelection().getDefinition().getId());
 row.add(scenarioReport.getClassifierSelection().getReason());
 return row;
 }
}

