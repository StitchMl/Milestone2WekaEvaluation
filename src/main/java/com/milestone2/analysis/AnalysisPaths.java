package com.milestone2.analysis;

import java.nio.file.Path;

/**
 * File-system locations used by one analysis execution.
 */
public class AnalysisPaths {
    private final Path dataDir;
    private final Path outputDir;
    private final Path resultsCsv;
    private final Path foldCsv;
    private final Path milestone2SummaryCsv;
    private final Path featureCorrelationsCsv;
    private final Path whatIfSummaryCsv;
    private final Path chartsDir;
    private final Path classifierConfigPath;

    public AnalysisPaths(Path dataDir, Path outputDir, Path classifierConfigPath) {
        this(
                dataDir.normalize(),
                outputDir.normalize(),
                outputDir.resolve(Config.RESULTS_CSV).normalize(),
                outputDir.resolve(Config.FOLD_CSV).normalize(),
                outputDir.resolve(Config.MILESTONE2_SUMMARY_CSV).normalize(),
                outputDir.resolve(Config.FEATURE_CORRELATIONS_CSV).normalize(),
                outputDir.resolve(Config.WHAT_IF_SUMMARY_CSV).normalize(),
                outputDir.resolve(Config.CHARTS_DIR).normalize(),
                classifierConfigPath.normalize()
        );
    }

    AnalysisPaths(Path dataDir,
                  Path outputDir,
                  Path resultsCsv,
                  Path foldCsv,
                  Path milestone2SummaryCsv,
                  Path featureCorrelationsCsv,
                  Path whatIfSummaryCsv,
                  Path chartsDir,
                  Path classifierConfigPath) {
        this.dataDir = dataDir;
        this.outputDir = outputDir;
        this.resultsCsv = resultsCsv;
        this.foldCsv = foldCsv;
        this.milestone2SummaryCsv = milestone2SummaryCsv;
        this.featureCorrelationsCsv = featureCorrelationsCsv;
        this.whatIfSummaryCsv = whatIfSummaryCsv;
        this.chartsDir = chartsDir;
        this.classifierConfigPath = classifierConfigPath;
    }

    public Path getDataDir() {
        return dataDir;
    }

    public Path getOutputDir() {
        return outputDir;
    }

    public Path getResultsCsv() {
        return resultsCsv;
    }

    public Path getFoldCsv() {
        return foldCsv;
    }

    public Path getMilestone2SummaryCsv() {
        return milestone2SummaryCsv;
    }

    public Path getFeatureCorrelationsCsv() {
        return featureCorrelationsCsv;
    }

    public Path getWhatIfSummaryCsv() {
        return whatIfSummaryCsv;
    }

    public Path getChartsDir() {
        return chartsDir;
    }

    public Path getClassifierConfigPath() {
        return classifierConfigPath;
    }
}

