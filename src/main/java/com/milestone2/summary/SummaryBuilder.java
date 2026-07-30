package com.milestone2.summary;

import com.milestone2.classifier.OverallWinnerSelector;
import com.milestone2.dataset.AnalysisReport;
import com.milestone2.metric.MetricWinnerSelector;

/**
 * Builds the milestone-oriented view of one dataset analysis.
 */
public class SummaryBuilder {
 private final MetricWinnerSelector metricWinnerSelector;
 private final OverallWinnerSelector overallWinnerSelector;

 public SummaryBuilder() {
 this(new MetricWinnerSelector(), new OverallWinnerSelector());
 }

 SummaryBuilder(MetricWinnerSelector metricWinnerSelector,
 OverallWinnerSelector overallWinnerSelector) {
 this.metricWinnerSelector = metricWinnerSelector;
 this.overallWinnerSelector = overallWinnerSelector;
 }

 /**
 * Builds the milestone summary derived from the dataset evaluation report.
 *
 * @param report dataset analysis report
 * @return milestone summary
 */
 public Summary build(AnalysisReport report) {
 return new Summary(
 metricWinnerSelector.select(report),
 overallWinnerSelector.select(report)
 );
 }
}

