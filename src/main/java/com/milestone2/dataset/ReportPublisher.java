package com.milestone2.dataset;

import com.milestone2.csvExporter.ChartGenerator;
import com.milestone2.startupUtility.RunConfig;
import com.milestone2.startupUtility.OutputWriters;
import com.milestone2.classifier.EvaluationReport;
import com.milestone2.metric.BestMetricLogger;
import com.milestone2.summary.Summary;
import com.milestone2.summary.SummaryBuilder;

import java.io.IOException;

/**
 * Publishes one dataset report to CSV outputs, charts and logs.
 */
public class ReportPublisher {
 private final ChartGenerator chartGenerator;
 private final BestMetricLogger bestMetricLogger;
 private final SummaryBuilder milestone2SummaryBuilder;

 public ReportPublisher(ChartGenerator chartGenerator, BestMetricLogger bestMetricLogger) {
 this(chartGenerator, bestMetricLogger, new SummaryBuilder());
 }

 ReportPublisher(ChartGenerator chartGenerator,
 BestMetricLogger bestMetricLogger,
 SummaryBuilder milestone2SummaryBuilder) {
 this.chartGenerator = chartGenerator;
 this.bestMetricLogger = bestMetricLogger;
 this.milestone2SummaryBuilder = milestone2SummaryBuilder;
 }

 /**
 * Publishes one dataset report to all configured CSV outputs, generated charts and summary logs.
 *
 * @param config immutable analysis configuration
 * @param report dataset analysis report
 * @param outputs opened output writers bundle
 * @throws IOException when any output cannot be written
 */
 public void publish(RunConfig config,
 AnalysisReport report,
 OutputWriters outputs) throws IOException {
 Summary milestone2Summary = milestone2SummaryBuilder.build(report);
 for (EvaluationReport classifierReport : report.getClassifierReports()) {
 outputs.getResultsWriter().write(
 config,
 report.getDatasetName(),
 report.getClassAttributeName(),
 report.getPositiveClassValue(),
 classifierReport.getDefinition(),
 classifierReport.getAggregateMetrics()
 );
 outputs.getFoldResultsWriter().write(
 config,
 report.getDatasetName(),
 report.getClassAttributeName(),
 report.getPositiveClassValue(),
 classifierReport.getDefinition(),
 classifierReport.getFoldResults()
 );
 }

 outputs.getMilestone2SummaryWriter().write(config, report, milestone2Summary);
 if (outputs.hasWhatIfOutputs()) {
 outputs.getWhatIfOutputs().getFeatureCorrelationWriter().write(config, report);
 outputs.getWhatIfOutputs().getWhatIfSummaryWriter().write(config, report);
 }
 chartGenerator.generate(report);
 bestMetricLogger.log(report);
 }
}

