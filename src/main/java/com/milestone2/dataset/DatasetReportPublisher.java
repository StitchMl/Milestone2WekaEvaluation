package com.milestone2.dataset;

import com.milestone2.report.ChartGenerator;
import com.milestone2.analysis.AnalysisConfig;
import com.milestone2.analysis.AnalysisOutputs;
import com.milestone2.classifier.ClassifierEvaluationReport;
import com.milestone2.metric.BestMetricLogger;
import com.milestone2.summary.Milestone2Summary;
import com.milestone2.summary.Milestone2SummaryBuilder;

import java.io.IOException;

/**
 * Publishes one dataset report to CSV outputs, charts and logs.
 */
public class DatasetReportPublisher {
    private final ChartGenerator chartGenerator;
    private final BestMetricLogger bestMetricLogger;
    private final Milestone2SummaryBuilder milestone2SummaryBuilder;

    public DatasetReportPublisher(ChartGenerator chartGenerator, BestMetricLogger bestMetricLogger) {
        this(chartGenerator, bestMetricLogger, new Milestone2SummaryBuilder());
    }

    DatasetReportPublisher(ChartGenerator chartGenerator,
                           BestMetricLogger bestMetricLogger,
                           Milestone2SummaryBuilder milestone2SummaryBuilder) {
        this.chartGenerator = chartGenerator;
        this.bestMetricLogger = bestMetricLogger;
        this.milestone2SummaryBuilder = milestone2SummaryBuilder;
    }

    public void publish(AnalysisConfig config,
                        DatasetAnalysisReport report,
                        AnalysisOutputs outputs) throws IOException {
        Milestone2Summary milestone2Summary = milestone2SummaryBuilder.build(report);
        for (ClassifierEvaluationReport classifierReport : report.getClassifierReports()) {
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

