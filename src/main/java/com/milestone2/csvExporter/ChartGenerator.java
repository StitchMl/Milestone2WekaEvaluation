package com.milestone2.csvExporter;

import com.milestone2.dataset.AnalysisReport;
import com.milestone2.foldMetadata.FoldDistributionChart;
import com.milestone2.metric.MetricChartData;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Generates charts from aggregate metrics and real per-fold distributions.
 */
public class ChartGenerator {
 private static final Logger log = LoggerFactory.getLogger(ChartGenerator.class);
 private static final String VALUE = "Value";

 private final Path chartsDir;
 private final MetricChartData categoryDatasetFactory;
 private final FoldDistributionChart foldDistributionDatasetFactory;

 public ChartGenerator(Path chartsDir) {
 this(chartsDir, new MetricChartData(), new FoldDistributionChart());
 }

 ChartGenerator(Path chartsDir,
 MetricChartData categoryDatasetFactory,
 FoldDistributionChart foldDistributionDatasetFactory) {
 this.chartsDir = chartsDir;
 this.categoryDatasetFactory = categoryDatasetFactory;
 this.foldDistributionDatasetFactory = foldDistributionDatasetFactory;
 }

 /**
 * Generates the aggregate bar chart and the per-fold box plot for one dataset report.
 *
 * @param report dataset analysis report
 * @throws IOException when a chart image cannot be written
 */
 public void generate(AnalysisReport report) throws IOException {
 log.info("Generating charts for dataset '{}'", report.getDatasetName());

 JFreeChart barChart = ChartFactory.createBarChart(
 "Metrics for " + report.getDatasetName(),
 "Metric",
 VALUE,
 categoryDatasetFactory.create(report)
 );

 String baseName = sanitizeDatasetName(report.getDatasetName());
 ChartUtils.saveChartAsPNG(chartsDir.resolve(baseName + "_bar.png").toFile(), barChart, 800, 600);

 JFreeChart boxChart = ChartFactory.createBoxAndWhiskerChart(
 "Fold Distribution for " + report.getDatasetName(),
 "Metric",
 VALUE,
 foldDistributionDatasetFactory.create(report),
 true
 );
 ChartUtils.saveChartAsPNG(chartsDir.resolve(baseName + "_box.png").toFile(), boxChart, 900, 600);
 }

 /**
 * Removes the dataset extension to obtain the base filename for generated charts.
 *
 * @param datasetName dataset name
 * @return chart base filename
 */
 private String sanitizeDatasetName(String datasetName) {
 return datasetName.replaceFirst("(?i)\\.(csv|arff)$", "");
 }
}

