package com.milestone2.report;

import com.milestone2.analysis.AnalysisConfig;
import com.milestone2.analysis.AnalysisExecution;
import com.milestone2.analysis.AnalysisSelection;
import com.milestone2.classifier.ClassifierDefinition;

import java.util.Collections;
import java.util.List;

/**
 * Shared CSV header and row-prefix helpers used by result export writers.
 *
 * <p>Both {@link ResultsWriter} and
 * {@link com.milestone2.fold.FoldResultsWriter} open their exports with the
 * same thirteen execution-metadata columns. Centralising them here avoids
 * duplication and keeps the two writers in sync automatically.
 */
public final class CsvResultsSupport {

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

 private CsvResultsSupport() {
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
 AnalysisConfig config,
 String datasetName,
 String classAttribute,
 String positiveClass,
 ClassifierDefinition definition) {
 AnalysisExecution execution = config.getExecution();
 AnalysisSelection selection = config.getSelection();
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
