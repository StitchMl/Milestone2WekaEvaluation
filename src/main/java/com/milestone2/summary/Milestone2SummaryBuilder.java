package com.milestone2.summary;

import com.milestone2.classifier.OverallClassifierWinnerSelector;
import com.milestone2.dataset.DatasetAnalysisReport;
import com.milestone2.metric.MetricWinnerSelector;

/**
 * Builds the milestone-oriented view of one dataset analysis.
 */
public class Milestone2SummaryBuilder {
    private final MetricWinnerSelector metricWinnerSelector;
    private final OverallClassifierWinnerSelector overallWinnerSelector;

    public Milestone2SummaryBuilder() {
        this(new MetricWinnerSelector(), new OverallClassifierWinnerSelector());
    }

    Milestone2SummaryBuilder(MetricWinnerSelector metricWinnerSelector,
                             OverallClassifierWinnerSelector overallWinnerSelector) {
        this.metricWinnerSelector = metricWinnerSelector;
        this.overallWinnerSelector = overallWinnerSelector;
    }

    /**
     * Builds the milestone summary derived from the dataset evaluation report.
     *
     * @param report dataset analysis report
     * @return milestone summary
     */
    public Milestone2Summary build(DatasetAnalysisReport report) {
        return new Milestone2Summary(
                metricWinnerSelector.select(report),
                overallWinnerSelector.select(report)
        );
    }
}

