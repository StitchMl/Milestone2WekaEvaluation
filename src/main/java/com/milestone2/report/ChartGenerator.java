package com.milestone2.report;

import com.milestone2.dataset.DatasetAnalysisReport;
import com.milestone2.fold.FoldDistributionDatasetFactory;
import com.milestone2.metric.MetricCategoryDatasetFactory;
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
    private final MetricCategoryDatasetFactory categoryDatasetFactory;
    private final FoldDistributionDatasetFactory foldDistributionDatasetFactory;

    public ChartGenerator(Path chartsDir) {
        this(chartsDir, new MetricCategoryDatasetFactory(), new FoldDistributionDatasetFactory());
    }

    ChartGenerator(Path chartsDir,
                   MetricCategoryDatasetFactory categoryDatasetFactory,
                   FoldDistributionDatasetFactory foldDistributionDatasetFactory) {
        this.chartsDir = chartsDir;
        this.categoryDatasetFactory = categoryDatasetFactory;
        this.foldDistributionDatasetFactory = foldDistributionDatasetFactory;
    }

    public void generate(DatasetAnalysisReport report) throws IOException {
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

    private String sanitizeDatasetName(String datasetName) {
        return datasetName.replaceFirst("(?i)\\.(csv|arff)$", "");
    }
}


