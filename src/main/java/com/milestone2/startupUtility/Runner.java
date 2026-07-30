package com.milestone2.startupUtility;

import com.milestone2.csvExporter.ChartGenerator;
import com.milestone2.classifier.Catalog;
import com.milestone2.dataset.AnalysisReport;
import com.milestone2.dataset.Analyzer;
import com.milestone2.dataset.Discovery;
import com.milestone2.dataset.ReportPublisher;
import com.milestone2.metric.BestMetricLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;

/**
 * Coordinates dataset discovery, evaluation, reporting and chart generation.
 */
public class Runner {
 private static final Logger log = LoggerFactory.getLogger(Runner.class);

 private final Discovery datasetDiscovery;
 private final Analyzer datasetAnalyzer;
 private final BestMetricLogger bestMetricLogger;

 public Runner() {
 this(new Discovery(),
 new Analyzer(),
 new BestMetricLogger());
 }

 Runner(Discovery datasetDiscovery,
 Analyzer datasetAnalyzer,
 BestMetricLogger bestMetricLogger) {
 this.datasetDiscovery = datasetDiscovery;
 this.datasetAnalyzer = datasetAnalyzer;
 this.bestMetricLogger = bestMetricLogger;
 }

 /**
 * Discovers datasets, analyzes each one and publishes the resulting reports.
 *
 * @param config immutable analysis configuration
 * @param classifierCatalog classifiers selected for the run
 * @param outputs opened output writers bundle
 * @throws Exception when dataset discovery, analysis or publishing fails
 */
 public void run(RunConfig config,
 Catalog classifierCatalog,
 OutputWriters outputs) throws Exception {
 ResolvedPaths paths = config.getPaths();
 List<Path> datasetFiles = datasetDiscovery.list(paths.getDataDir());
 if (datasetFiles.isEmpty()) {
 log.warn("No CSV/ARFF dataset found in '{}'", paths.getDataDir());
 return;
 }

 ReportPublisher reportPublisher = new ReportPublisher(
 new ChartGenerator(paths.getChartsDir()),
 bestMetricLogger
 );
 for (Path datasetFile : datasetFiles) {
 AnalysisReport report = datasetAnalyzer.analyze(datasetFile, config, classifierCatalog);
 reportPublisher.publish(config, report, outputs);
 }
 }
}

