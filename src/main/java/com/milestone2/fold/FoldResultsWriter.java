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
            List<Object> record = new ArrayList<>();
            record.add(execution.getRunId());
            record.add(selection.getGranularity());
            record.add(datasetName);
            record.add(definition.getDisplayName());
            record.add(definition.getClassName());
            record.add(classAttribute);
            record.add(positiveClass);
            record.add(selection.getSizeAttributeName());
            record.add(execution.getSeed());
            record.add(result.getRun());
            record.add(result.getFold());
            for (MetricDefinition metric : MetricDefinition.values()) {
                record.add(metric.extract(metrics));
            }
            printer.printRecord(record);
        }
        printer.flush();
    }

    private static String[] buildHeader() {
        List<String> header = new ArrayList<>();
        header.add("RunId");
        header.add("Granularity");
        header.add("Dataset");
        header.add("Classifier");
        header.add("ClassifierClass");
        header.add("ClassAttribute");
        header.add("PositiveClass");
        header.add("SizeAttribute");
        header.add("Seed");
        header.add("Run");
        header.add("Fold");
        for (MetricDefinition metric : MetricDefinition.values()) {
            header.add(metric.getDisplayName());
        }
        return header.toArray(new String[0]);
    }

    @Override
    public void close() throws IOException {
        printer.close();
    }
}

