package com.milestone2.metric;

import com.milestone2.dataset.AnalysisReport;
import com.milestone2.classifier.EvaluationReport;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Builds the aggregate metrics dataset used by the bar chart.
 */
public class MetricChartData {
 /**
 * Builds the dataset consumed by the aggregate metrics bar chart.
 *
 * @param report dataset analysis report
 * @return normalized category dataset for chart generation
 */
 public CategoryDataset create(AnalysisReport report) {
 DefaultCategoryDataset dataset = new DefaultCategoryDataset();
 for (EvaluationReport classifierReport : report.getClassifierReports()) {
 for (MetricDefinition metric : MetricDefinition.values()) {
 Double value = classifierReport.getAggregateMetrics().get(metric);
 if (value != null && !Double.isNaN(value)) {
 dataset.addValue(
 metric.normalizeForChart(value),
 classifierReport.getDefinition().getDisplayName(),
 metric.getDisplayName()
 );
 }
 }
 }
 return dataset;
 }
}

