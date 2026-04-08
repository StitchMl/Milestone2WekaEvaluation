package com.milestone2.summary;

import com.milestone2.analysis.AnalysisConfig;
import com.milestone2.dataset.DatasetAnalysisReport;
import com.milestone2.metric.MetricWinner;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Writes the milestone winners per metric plus the overall winner.
 */
public class Milestone2SummaryWriter implements AutoCloseable {
    private static final String[] HEADER = {
            "RunId",
            "Granularity",
            "Dataset",
            "ClassAttribute",
            "PositiveClass",
            "RowType",
            "Metric",
            "Classifier",
            "ClassifierId",
            "ClassifierClass",
            "MetricValue",
            "Kappa",
            "AUC",
            "Reason"
    };

    private final CSVPrinter printer;
    private final Milestone2SummaryRecordFactory recordFactory;

    public Milestone2SummaryWriter(Path file) throws IOException {
        Writer out = Files.newBufferedWriter(file, StandardCharsets.UTF_8);
        printer = new CSVPrinter(out, CSVFormat.DEFAULT.builder().setHeader(HEADER).get());
        recordFactory = new Milestone2SummaryRecordFactory();
    }

    public void write(AnalysisConfig config,
                      DatasetAnalysisReport report,
                      Milestone2Summary summary) throws IOException {
        for (MetricWinner winner : summary.getMetricWinners()) {
            printer.printRecord(recordFactory.metricWinnerRecord(config, report, winner));
        }
        if (summary.getOverallWinner() != null) {
            printer.printRecord(recordFactory.overallWinnerRecord(config, report, summary.getOverallWinner()));
        }
        printer.flush();
    }

    @Override
    public void close() throws IOException {
        printer.close();
    }
}

