package com.milestone2;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Generates a comparative bar graph for each dataset
 * between the given classifiers and metrics.
 */
public class ChartGenerator {

    /** Default metrics to be plotted in the desired order */
    private static final List<String> DEFAULT_METRICS = Arrays.asList(
            "Accuracy", "Precision", "Recall", "F1",
            "Kappa", "AUC", "NPofB20"
    );

    /**
     * Create and save the graph.
     *
     * @param datasetName dataset name (used in title and filename)
     * @param results map <Classifier, map<Metric,Value>>
     * @throws IOException if file write fails
     */
    public void generate(String datasetName,
                         Map<String, Map<String, Double>> results)
            throws IOException {

        DefaultCategoryDataset cat = createDataset(results);

        JFreeChart chart = ChartFactory.createBarChart(
                "Confronto Classificatori – " + datasetName,
                "Metrica",
                "Valore",
                cat,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        File out = new File(Config.CHARTS_DIR + datasetName + ".png");
        ChartUtils.saveChartAsPNG(out, chart, 1000, 600);
    }

    /**
     * Constructs the CategoryDataset for JFreeChart.
     *
     * @param results map <Classifier, map<Metric,Value>>
     * @return dataset ready for ChartFactory
     */
    private DefaultCategoryDataset createDataset(
            Map<String, Map<String, Double>> results) {

        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        // rowKey = classifier, columnKey = metric
        for (Map.Entry<String, Map<String, Double>> entry : results.entrySet()) {
            String clf = entry.getKey();
            Map<String, Double> met = entry.getValue();
            for (String metric : ChartGenerator.DEFAULT_METRICS) {
                Double v = met.get(metric);
                if (v != null) {
                    ds.addValue(v, clf, metric);
                }
            }
        }
        return ds;
    }
}