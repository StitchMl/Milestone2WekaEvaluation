package com.milestone2.whatif;

import com.milestone2.classifier.ClassifierEvaluationReport;
import com.milestone2.metric.MetricDefinition;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Selects the classifier to be used in the what-if study.
 */
public class WhatIfClassifierSelector {
    /**
     * Selects the classifier used to run the what-if scenarios, honoring explicit CLI selection when present.
     *
     * @param options           what-if options
     * @param classifierReports evaluated classifier reports
     * @return selected classifier and selection reason
     */
    public WhatIfClassifierSelection select(WhatIfOptions options,
                                            List<ClassifierEvaluationReport> classifierReports) {
        if (options.getClassifierId() != null) {
            return explicitSelection(options.getClassifierId(), classifierReports);
        }

        ClassifierEvaluationReport bestReport = classifierReports.stream()
                .max(Comparator
                        .comparingDouble((ClassifierEvaluationReport report) ->
                                metric(report.getAggregateMetrics(), MetricDefinition.KAPPA))
                        .thenComparingDouble(report ->
                                metric(report.getAggregateMetrics(), MetricDefinition.AUC))
                        .thenComparing(report -> report.getDefinition().getDisplayName()))
                .orElseThrow(() -> new IllegalArgumentException("No classifier report available for what-if analysis"));

        return new WhatIfClassifierSelection(
                bestReport.getDefinition(),
                "best validation classifier by Kappa, then AUC"
        );
    }

    /**
     * Resolves the classifier explicitly requested by the user.
     *
     * @param classifierId      requested classifier identifier
     * @param classifierReports evaluated classifier reports
     * @return explicit classifier selection
     */
    private WhatIfClassifierSelection explicitSelection(String classifierId,
                                                        List<ClassifierEvaluationReport> classifierReports) {
        for (ClassifierEvaluationReport report : classifierReports) {
            if (report.getDefinition().getId().equalsIgnoreCase(classifierId)) {
                return new WhatIfClassifierSelection(report.getDefinition(), "explicit CLI selection");
            }
        }
        throw new IllegalArgumentException("What-if classifier '" + classifierId + "' not found in the selected catalog");
    }

    /**
     * Reads one aggregate metric value from the classifier report metrics map.
     *
     * @param metrics metric map
     * @param metric  metric to extract
     * @return metric value
     */
    private double metric(Map<MetricDefinition, Double> metrics, MetricDefinition metric) {
        return metrics.get(metric);
    }
}

