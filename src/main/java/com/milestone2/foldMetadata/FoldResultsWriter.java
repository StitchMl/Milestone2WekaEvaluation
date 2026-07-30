package com.milestone2.foldMetadata;

import com.milestone2.startupUtility.RunConfig;
import com.milestone2.classifier.Definition;
import com.milestone2.metric.MetricDefinition;
import com.milestone2.metric.Metrics;
import com.milestone2.csvExporter.CsvSupport;
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
 public void write(RunConfig config,
 String datasetName,
 String classAttribute,
 String positiveClass,
 Definition definition,
 List<FoldResult> foldResults) throws IOException {
 for (FoldResult result : foldResults) {
 Metrics metrics = result.getMetrics();
 List<Object> row = new ArrayList<>();
 CsvSupport.addBaseFields(row, config, datasetName, classAttribute, positiveClass, definition);
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
 CsvSupport.addBaseColumns(header);
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
