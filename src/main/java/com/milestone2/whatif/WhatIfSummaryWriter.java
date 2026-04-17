package com.milestone2.whatif;

import com.milestone2.prediction.ScenarioPredictionSummary;
import com.milestone2.analysis.AnalysisConfig;
import com.milestone2.dataset.DatasetAnalysisReport;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Writes scenario-level and paired-impact what-if summaries.
 */
public class WhatIfSummaryWriter implements AutoCloseable {
    private static final String[] HEADER = {
            "RunId",
            "Granularity",
            "Dataset",
            "ValidationStrategy",
            "TemporalAttribute",
            "ClassAttribute",
            "PositiveClass",
            "Feature",
            "FeatureSelectionReason",
            "Classifier",
            "ClassifierId",
            "ClassifierSelectionReason",
            "RowType",
            "Scenario",
            "Instances",
            "ActualBuggy",
            "PredictedBuggy",
            "AveragePositiveProbability",
            "PredictedRelieved",
            "AvoidableBuggy",
            "AvoidableBuggyShare",
            "AveragePositiveProbabilityReduction"
    };

    private final CSVPrinter printer;
    private final WhatIfSummaryRecordFactory recordFactory;

    public WhatIfSummaryWriter(Path file) throws IOException {
        Writer out = Files.newBufferedWriter(file, StandardCharsets.UTF_8);
        printer = new CSVPrinter(out, CSVFormat.DEFAULT.builder().setHeader(HEADER).get());
        recordFactory = new WhatIfSummaryRecordFactory();
    }

    /**
     * Writes scenario-level and impact-level what-if rows for the dataset when scenario results are available.
     *
     * @param config immutable analysis configuration
     * @param report dataset analysis report
     * @throws IOException when the CSV output cannot be written
     */
    public void write(AnalysisConfig config, DatasetAnalysisReport report) throws IOException {
        if (!report.hasWhatIfReport() || !report.getWhatIfReport().hasScenarioReport()) {
            return;
        }

        WhatIfScenarioReport scenarioReport = report.getWhatIfReport().getScenarioReport();
        for (ScenarioPredictionSummary summary : scenarioReport.getScenarioSummaries()) {
            printer.printRecord(recordFactory.buildScenarioRecord(config, report, scenarioReport, summary));
        }
        printer.printRecord(recordFactory.buildImpactRecord(config, report, scenarioReport));
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

