package com.milestone2.fold;

import com.milestone2.classifier.ClassifierEvaluationReport;
import com.milestone2.dataset.DatasetAnalysisReport;
import com.milestone2.metric.MetricDefinition;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Builds the per-fold distribution dataset used by the box plot chart.
 */
public class FoldDistributionDatasetFactory {
    /**
     * Builds the box-plot dataset containing normalized per-fold metric values for every classifier.
     *
     * @param report dataset analysis report
     * @return box-and-whisker dataset for chart generation
     */
    public BoxAndWhiskerCategoryDataset create(DatasetAnalysisReport report) {
        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
        for (ClassifierEvaluationReport classifierReport : report.getClassifierReports()) {
            String classifier = classifierReport.getDefinition().getDisplayName();
            for (MetricDefinition metric : MetricDefinition.values()) {
                List<Double> values = classifierReport.getFoldResults().stream()
                        .map(result -> metric.extract(result.getMetrics()))
                        .filter(value -> !Double.isNaN(value))
                        .map(metric::normalizeForChart)
                        .collect(Collectors.toList());
                if (!values.isEmpty()) {
                    dataset.add(values, classifier, metric.getDisplayName());
                }
            }
        }
        return dataset;
    }
}

