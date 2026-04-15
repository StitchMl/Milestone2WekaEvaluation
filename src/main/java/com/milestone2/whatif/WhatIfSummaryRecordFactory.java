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
    public List<Object> buildScenarioRecord(AnalysisConfig config,
                                            DatasetAnalysisReport report,
                                            WhatIfScenarioReport scenarioReport,
                                            ScenarioPredictionSummary summary) {
        List<Object> record = commonRecord(config, report, scenarioReport);
        record.add("SCENARIO");
        record.add(summary.getScenario().getDisplayName());
        record.add(summary.getInstanceCount());
        record.add(summary.getActualBuggyCount());
        record.add(summary.getPredictedBuggyCount());
        record.add(summary.getAveragePositiveProbability());
        record.add(null);
        record.add(null);
        record.add(null);
        record.add(null);
        return record;
    }

    public List<Object> buildImpactRecord(AnalysisConfig config,
                                          DatasetAnalysisReport report,
                                          WhatIfScenarioReport scenarioReport) {
        WhatIfImpactSummary impact = scenarioReport.getImpactSummary();
        List<Object> record = commonRecord(config, report, scenarioReport);
        record.add("IMPACT");
        record.add("B+->B");
        record.add(impact.getPairedInstanceCount());
        record.add(impact.getActualBuggyCount());
        record.add(null);
        record.add(null);
        record.add(impact.getPredictedRelievedCount());
        record.add(impact.getAvoidableBuggyCount());
        record.add(impact.getAvoidableBuggyShare());
        record.add(impact.getAveragePositiveProbabilityReduction());
        return record;
    }

    private List<Object> commonRecord(AnalysisConfig config,
                                      DatasetAnalysisReport report,
                                      WhatIfScenarioReport scenarioReport) {
        List<Object> record = new ArrayList<>();
        record.add(config.getExecution().getRunId());
        record.add(config.getSelection().getGranularity());
        record.add(report.getDatasetName());
        record.add(config.getExecution().getValidationStrategy().getCliValue());
        record.add(config.getExecution().getTemporalAttributeName());
        record.add(report.getClassAttributeName());
        record.add(report.getPositiveClassValue());
        record.add(scenarioReport.getFeatureSelection().getFeatureName());
        record.add(scenarioReport.getFeatureSelection().getReason());
        record.add(scenarioReport.getClassifierSelection().getDefinition().getDisplayName());
        record.add(scenarioReport.getClassifierSelection().getDefinition().getId());
        record.add(scenarioReport.getClassifierSelection().getReason());
        return record;
    }
}

