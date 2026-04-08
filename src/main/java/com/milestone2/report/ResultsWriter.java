package com.milestone2.report;

import com.milestone2.analysis.AnalysisConfig;
import com.milestone2.analysis.AnalysisExecution;
import com.milestone2.analysis.AnalysisSelection;
import com.milestone2.classifier.ClassifierDefinition;
import com.milestone2.metric.MetricDefinition;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Writes aggregate results to CSV with execution metadata.
 */
public class ResultsWriter implements AutoCloseable {
    private static final String[] HEADER = buildHeader();

    private final CSVPrinter printer;

    public ResultsWriter(Path file) throws IOException {
        Writer out = Files.newBufferedWriter(file, StandardCharsets.UTF_8);
        printer = new CSVPrinter(out, CSVFormat.DEFAULT.builder().setHeader(HEADER).get());
    }

    public void write(AnalysisConfig config,
                      String datasetName,
                      String classAttribute,
                      String positiveClass,
                      ClassifierDefinition definition,
                      Map<MetricDefinition, Double> metrics) throws IOException {
        AnalysisExecution execution = config.getExecution();
        AnalysisSelection selection = config.getSelection();
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
        for (MetricDefinition metric : MetricDefinition.values()) {
            record.add(metrics.get(metric));
        }
        printer.printRecord(record);
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


