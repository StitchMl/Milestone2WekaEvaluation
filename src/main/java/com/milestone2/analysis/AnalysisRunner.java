package com.milestone2.analysis;

import com.milestone2.report.ChartGenerator;
import com.milestone2.classifier.ClassifierCatalog;
import com.milestone2.dataset.DatasetAnalysisReport;
import com.milestone2.dataset.DatasetAnalyzer;
import com.milestone2.dataset.DatasetDiscovery;
import com.milestone2.dataset.DatasetReportPublisher;
import com.milestone2.metric.BestMetricLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;

/**
 * Coordinates dataset discovery, evaluation, reporting and chart generation.
 */
public class AnalysisRunner {
    private static final Logger log = LoggerFactory.getLogger(AnalysisRunner.class);

    private final DatasetDiscovery datasetDiscovery;
    private final DatasetAnalyzer datasetAnalyzer;
    private final BestMetricLogger bestMetricLogger;

    public AnalysisRunner() {
        this(new DatasetDiscovery(),
                new DatasetAnalyzer(),
                new BestMetricLogger());
    }

    AnalysisRunner(DatasetDiscovery datasetDiscovery,
                   DatasetAnalyzer datasetAnalyzer,
                   BestMetricLogger bestMetricLogger) {
        this.datasetDiscovery = datasetDiscovery;
        this.datasetAnalyzer = datasetAnalyzer;
        this.bestMetricLogger = bestMetricLogger;
    }

    public void run(AnalysisConfig config,
                    ClassifierCatalog classifierCatalog,
                    AnalysisOutputs outputs) throws Exception {
        AnalysisPaths paths = config.getPaths();
        List<Path> datasetFiles = datasetDiscovery.list(paths.getDataDir());
        if (datasetFiles.isEmpty()) {
            log.warn("No CSV/ARFF dataset found in '{}'", paths.getDataDir());
            return;
        }

        DatasetReportPublisher reportPublisher = new DatasetReportPublisher(
                new ChartGenerator(paths.getChartsDir()),
                bestMetricLogger
        );
        for (Path datasetFile : datasetFiles) {
            DatasetAnalysisReport report = datasetAnalyzer.analyze(datasetFile, config, classifierCatalog);
            reportPublisher.publish(config, report, outputs);
        }
    }
}

