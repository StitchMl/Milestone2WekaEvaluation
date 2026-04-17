package com.milestone2.feature;

import com.milestone2.whatif.WhatIfAnalysisReport;
import com.milestone2.analysis.AnalysisConfig;
import com.milestone2.dataset.DatasetAnalysisReport;
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
 * Writes feature correlation rankings for each analyzed dataset.
 */
public class FeatureCorrelationWriter implements AutoCloseable {
    private static final String[] HEADER = {
            "RunId",
            "Granularity",
            "Dataset",
            "ValidationStrategy",
            "TemporalAttribute",
            "ClassAttribute",
            "PositiveClass",
            "Feature",
            "Correlation",
            "AbsCorrelation",
            "NonMissingValues",
            "ZeroValues",
            "PositiveValues",
            "SelectedForWhatIf",
            "SelectionReason"
    };

    private final CSVPrinter printer;

    public FeatureCorrelationWriter(Path file) throws IOException {
        Writer out = Files.newBufferedWriter(file, StandardCharsets.UTF_8);
        printer = new CSVPrinter(out, CSVFormat.DEFAULT.builder().setHeader(HEADER).get());
    }

    /**
     * Writes the feature correlation ranking for the dataset when a what-if report is available.
     *
     * @param config immutable analysis configuration
     * @param report dataset analysis report
     * @throws IOException when the CSV output cannot be written
     */
    public void write(AnalysisConfig config, DatasetAnalysisReport report) throws IOException {
        if (!report.hasWhatIfReport()) {
            return;
        }

        WhatIfAnalysisReport whatIfReport = report.getWhatIfReport();
        String selectedFeature = null;
        String selectionReason = null;
        if (whatIfReport.hasScenarioReport()) {
            selectedFeature = whatIfReport.getScenarioReport().getFeatureSelection().getFeatureName();
            selectionReason = whatIfReport.getScenarioReport().getFeatureSelection().getReason();
        }

        for (FeatureCorrelation correlation : whatIfReport.getFeatureCorrelations()) {
            List<Object> record = new ArrayList<>();
            record.add(config.getExecution().getRunId());
            record.add(config.getSelection().getGranularity());
            record.add(report.getDatasetName());
            record.add(config.getExecution().getValidationStrategy().getCliValue());
            record.add(config.getExecution().getTemporalAttributeName());
            record.add(report.getClassAttributeName());
            record.add(report.getPositiveClassValue());
            record.add(correlation.getFeatureName());
            record.add(correlation.getCorrelation());
            record.add(correlation.getAbsoluteCorrelation());
            record.add(correlation.getNonMissingValueCount());
            record.add(correlation.getZeroValueCount());
            record.add(correlation.getPositiveValueCount());
            record.add(correlation.getFeatureName().equalsIgnoreCase(selectedFeature));
            record.add(correlation.getFeatureName().equalsIgnoreCase(selectedFeature)
                    ? selectionReason
                    : null);
            printer.printRecord(record);
        }
        printer.flush();
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
