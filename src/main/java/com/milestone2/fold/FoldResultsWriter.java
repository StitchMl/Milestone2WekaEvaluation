package com.milestone2.fold;

import com.milestone2.analysis.AnalysisConfig;
import com.milestone2.analysis.AnalysisExecution;
import com.milestone2.analysis.AnalysisSelection;
import com.milestone2.classifier.ClassifierDefinition;
import com.milestone2.metric.MetricDefinition;
import com.milestone2.metric.Metrics;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Writes per-fold metrics with execution metadata.
 */
public class FoldResultsWriter implements AutoCloseable {
 private static final String[] HEADER = buildHeader();

 private final CSVPrinter printer;

 public FoldResultsWriter(Path file) throws IOException {
 Writer out = Files.newBufferedWriter(file, StandardCharsets.UTF_8);
 printer = new CSVPrinter(out, CSVFormat.DEFAULT.builder().setHeader(HEADER).get());
 }

 /**
 * Writes one CSV row per evaluated fold enriched with execution and classifier metadata.
 *
 * @param config immutable analysis configuration
 * @param datasetName analyzed dataset name
 * @param classAttribute class attribute name
 * @param positiveClass positive class label
 * @param definition classifier definition
 * @param foldResults fold results to serialize
 * @throws IOException when the CSV output cannot be written
 */
 public void write(AnalysisConfig config,
 String datasetName,
 String classAttribute,
 String positiveClass,
 ClassifierDefinition definition,
 List<PerFoldResult> foldResults) throws IOException {
 AnalysisExecution execution = config.getExecution();
 AnalysisSelection selection = config.getSelection();
 for (PerFoldResult result : foldResults) {
 Metrics metrics = result.getMetrics();
 List<Object> row = new ArrayList<>();
 row.add(execution.getRunId());
 row.add(selection.getGranularity());
 row.add(datasetName);
 row.add(execution.getValidationStrategy().getCliValue());
 row.add(execution.getTemporalAttributeName());
 row.add(definition.getDisplayName());
 row.add(definition.getClassName());
 row.add(classAttribute);
 row.add(positiveClass);
 row.add(selection.getSizeAttributeName());
 row.add(execution.getSeed());
 row.add(execution.getBalancingStrategy().getCliValue());
 row.add(execution.getFeatureSelectionStrategy().getCliValue());
 row.add(result.getRun());
 row.add(result.getFold());
 row.add(result.getTrainingWindowLabel());
 row.add(result.getTestWindowLabel());
 row.add(result.getTrainingInstances() < 0 ? null : result.getTrainingInstances());
 row.add(result.getTestInstances() < 0 ? null : result.getTestInstances());
 for (MetricDefinition metric : MetricDefinition.values()) {
 row.add(metric.extract(metrics));
 }
 printer.printRecord(row);
 }
 printer.flush();
 }

 /**
 * Builds the CSV header used for per-fold exports, including one column per supported metric.
 *
 * @return CSV header
 */
 private static String[] buildHeader() {
 List<String> header = new ArrayList<>();
 header.add("RunId");
 header.add("Granularity");
 header.add("Dataset");
 header.add("ValidationStrategy");
 header.add("TemporalAttribute");
 header.add("Classifier");
 header.add("ClassifierClass");
 header.add("ClassAttribute");
 header.add("PositiveClass");
 header.add("SizeAttribute");
 header.add("Seed");
 header.add("Balancing");
 header.add("FeatureSelection");
 header.add("Run");
 header.add("Fold");
 header.add("TrainingWindow");
 header.add("TestWindow");
 header.add("TrainingInstances");
 header.add("TestInstances");
 for (MetricDefinition metric : MetricDefinition.values()) {
 header.add(metric.getDisplayName());
 }
 return header.toArray(new String[0]);
 }

 /**
 * Closes the underlying CSV printer.
 *
 * @throws IOException when closing the writer fails
 */
 @Override
 public void close() throws IOException {
 printer.close();
 }
}
