package com.milestone2.startupUtility;

import java.nio.file.Path;

/**
 * File-system locations used by one analysis execution.
 */
public class ResolvedPaths {
 private final Path dataDir;
 private final Path outputDir;
 private final Path resultsCsv;
 private final Path foldCsv;
 private final Path milestone2SummaryCsv;
 private final Path featureCorrelationsCsv;
 private final Path whatIfSummaryCsv;
 private final Path chartsDir;
 private final Path classifierConfigPath;

 public ResolvedPaths(Path dataDir, Path outputDir, Path classifierConfigPath) {
 this.dataDir = dataDir.normalize();
 this.outputDir = outputDir.normalize();
 this.resultsCsv = outputDir.resolve(Defaults.RESULTS_CSV).normalize();
 this.foldCsv = outputDir.resolve(Defaults.FOLD_CSV).normalize();
 this.milestone2SummaryCsv = outputDir.resolve(Defaults.MILESTONE2_SUMMARY_CSV).normalize();
 this.featureCorrelationsCsv = outputDir.resolve(Defaults.FEATURE_CORRELATIONS_CSV).normalize();
 this.whatIfSummaryCsv = outputDir.resolve(Defaults.WHAT_IF_SUMMARY_CSV).normalize();
 this.chartsDir = outputDir.resolve(Defaults.CHARTS_DIR).normalize();
 this.classifierConfigPath = classifierConfigPath.normalize();
 }

 /**
 * Returns the directory containing the input datasets.
 *
 * @return dataset directory
 */
 public Path getDataDir() {
 return dataDir;
 }

 /**
 * Returns the root output directory for the current run.
 *
 * @return output directory
 */
 public Path getOutputDir() {
 return outputDir;
 }

 /**
 * Returns the path of the aggregate results CSV export.
 *
 * @return aggregate results CSV path
 */
 public Path getResultsCsv() {
 return resultsCsv;
 }

 /**
 * Returns the path of the per-fold results CSV export.
 *
 * @return fold results CSV path
 */
 public Path getFoldCsv() {
 return foldCsv;
 }

 /**
 * Returns the path of the milestone summary CSV export.
 *
 * @return milestone summary CSV path
 */
 public Path getMilestone2SummaryCsv() {
 return milestone2SummaryCsv;
 }

 /**
 * Returns the path of the feature-correlation CSV export used by the what-if workflow.
 *
 * @return feature correlations CSV path
 */
 public Path getFeatureCorrelationsCsv() {
 return featureCorrelationsCsv;
 }

 /**
 * Returns the path of the what-if summary CSV export.
 *
 * @return what-if summary CSV path
 */
 public Path getWhatIfSummaryCsv() {
 return whatIfSummaryCsv;
 }

 /**
 * Returns the directory where charts are generated.
 *
 * @return charts directory
 */
 public Path getChartsDir() {
 return chartsDir;
 }

 /**
 * Returns the classifier catalog configuration file.
 *
 * @return classifier configuration path
 */
 public Path getClassifierConfigPath() {
 return classifierConfigPath;
 }
}
