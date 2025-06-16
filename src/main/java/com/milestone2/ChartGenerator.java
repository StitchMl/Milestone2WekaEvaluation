package com.milestone2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartUtils;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

/**
 * Utility class for generating charts (bar and box plots) from classification metrics.
 * <p>
 * This class provides methods to generate bar charts and box-and-whisker plots based on
 * classifier performance metrics for a given dataset.
 * </p>
 */
public class ChartGenerator {

    private static final Logger log = LoggerFactory.getLogger(ChartGenerator.class);
    private static final String VALUE = "Value";

    /**
     * Generates bar and box plot charts for the given dataset and metrics.
     *
     * @param datasetName the name of the dataset being processed
     * @param allMetrics  a map where each key is a classifier name and the value is a map of metric names to metric values
     * @throws IOException if an error occurs while saving the charts to files
     */
    public void generate(String datasetName, Map<String, Map<String, Double>> allMetrics) throws IOException {
        log.info("Generating charts for dataset '{}'", datasetName);

        // Create and save a bar chart
        CategoryDataset barDataset = createCategoryDataset(allMetrics);
        JFreeChart barChart = ChartFactory.createBarChart(
                "Metrics for " + datasetName, // chart title
                "Metric",                    // domain axis label
                VALUE,                     // range axis label
                barDataset
        );
        String barFileName = Config.CHARTS_DIR + File.separator + datasetName + "_bar.png";
        ChartUtils.saveChartAsPNG(new File(barFileName), barChart, 800, 600);
        log.debug("Bar chart saved to '{}'", barFileName);

        // Create and save a box plot chart
        generateBoxPlots(datasetName, allMetrics);
    }

    /**
     * Generates a box-and-whisker plot chart for the given dataset and metrics.
     * <p>
     * This method creates a box plot with one box per metric category, where each box
     * is generated from the classifier metric values for that metric.
     * </p>
     *
     * @param datasetName the name of the dataset being processed
     * @param allMetrics  a map where each key is a classifier name and the value is a map of metric names to metric values
     * @throws IOException if an error occurs while saving the chart to a file
     */
    public void generateBoxPlots(String datasetName, Map<String, Map<String, Double>> allMetrics) throws IOException {
        log.info("Generating box plots for dataset '{}'", datasetName);

        BoxAndWhiskerCategoryDataset boxDataset = createBoxAndWhiskerDataset(allMetrics);
        JFreeChart boxChart = ChartFactory.createBoxAndWhiskerChart(
                "Box Plot for " + datasetName,
                "Metric",
                VALUE,
                boxDataset,
                false
        );
        String boxFileName = Config.CHARTS_DIR + File.separator + datasetName + "_box.png";
        ChartUtils.saveChartAsPNG(new File(boxFileName), boxChart, 800, 600);
        log.debug("Box plot chart saved to '{}'", boxFileName);
    }

    /**
     * Creates a CategoryDataset for the bar chart from the metrics map.
     *
     * @param allMetrics a map of classifier names to (metric name -> metric value) maps
     * @return a CategoryDataset suitable for creating a bar chart
     */
    private CategoryDataset createCategoryDataset(Map<String, Map<String, Double>> allMetrics) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (allMetrics == null || allMetrics.isEmpty()) {
            return dataset;
        }
        // Use the metrics names from the first classifier as categories
        String firstClassifier = allMetrics.keySet().iterator().next();
        for (String metric : allMetrics.get(firstClassifier).keySet()) {
            for (Map.Entry<String, Map<String, Double>> entry : allMetrics.entrySet()) {
                String classifier = entry.getKey();
                Map<String, Double> metrics = entry.getValue();
                Double value = metrics.get(metric);
                if (value != null) {
                    dataset.addValue(value, classifier, metric);
                }
            }
        }
        return dataset;
    }

    /**
     * Creates a BoxAndWhiskerCategoryDataset for the box plot from the metrics map.
     * <p>
     * Each metric category will have one box, containing the values of that metric
     * for all classifiers.
     * </p>
     *
     * @param allMetrics a map of classifier names to (metric name -> metric value) maps
     * @return a BoxAndWhiskerCategoryDataset suitable for creating a box-and-whisker chart
     */
    private BoxAndWhiskerCategoryDataset createBoxAndWhiskerDataset(Map<String, Map<String, Double>> allMetrics) {
        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
        if (allMetrics == null || allMetrics.isEmpty()) {
            return dataset;
        }
        // Use the metrics names from the first classifier as categories
        String firstClassifier = allMetrics.keySet().iterator().next();
        for (String metric : allMetrics.get(firstClassifier).keySet()) {
            // Collect values of this metric from all classifiers
            java.util.List<Double> values = new java.util.ArrayList<>();
            for (Map<String, Double> metrics : allMetrics.values()) {
                Double v = metrics.get(metric);
                if (v != null) {
                    values.add(v);
                }
            }
            // Add the list of values as a single series (row) and the metric as the category
            dataset.add(values, VALUE, metric);
        }
        return dataset;
    }
}