package com.milestone2.metric;

import com.milestone2.dataset.DatasetAnalysisReport;
import com.milestone2.classifier.ClassifierEvaluationReport;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Builds the aggregate metrics dataset used by the bar chart.
 */
public class MetricCategoryDatasetFactory {
    public CategoryDataset create(DatasetAnalysisReport report) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (ClassifierEvaluationReport classifierReport : report.getClassifierReports()) {
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

