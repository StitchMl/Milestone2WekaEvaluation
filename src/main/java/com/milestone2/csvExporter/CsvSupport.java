package com.milestone2.csvExporter;

import com.milestone2.startupUtility.RunConfig;
import com.milestone2.startupUtility.ExecutionSettings;
import com.milestone2.startupUtility.SelectionSettings;
import com.milestone2.classifier.Definition;

import java.util.Collections;
import java.util.List;

/**
 * Shared CSV header and row-prefix helpers used by result export writers.
 *
 * <p>Both {@link ResultsWriter} and
 * {@link com.milestone2.foldMetadata.FoldResultsWriter} open their exports with the
 * same thirteen execution-metadata columns. Centralising them here avoids
 * duplication and keeps the two writers in sync automatically.
 */
public final class CsvSupport {

 private static final String[] BASE_COLUMNS = {
 "RunId",
 "Granularity",
 "Dataset",
 "ValidationStrategy",
 "TemporalAttribute",
 "Classifier",
 "ClassifierClass",
 "ClassAttribute",
 "PositiveClass",
 "SizeAttribute",
 "Seed",
 "Balancing",
 "FeatureSelection"
 };

 private CsvSupport() {
 }

 /**
 * Appends the thirteen shared execution-metadata column names to the
 * supplied header list.
 *
 * @param header mutable header list to extend
 */
 public static void addBaseColumns(List<String> header) {
 Collections.addAll(header, BASE_COLUMNS);
 }

 /**
 * Appends the thirteen shared execution-metadata values to the supplied
 * CSV row.
 *
 * @param row mutable row list to extend
 * @param config immutable analysis configuration
 * @param datasetName analyzed dataset name
 * @param classAttribute class attribute name
 * @param positiveClass positive class label
 * @param definition classifier definition
 */
 public static void addBaseFields(List<Object> row,
 RunConfig config,
 String datasetName,
 String classAttribute,
 String positiveClass,
 Definition definition) {
 ExecutionSettings execution = config.getExecution();
 SelectionSettings selection = config.getSelection();
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
 }
}
