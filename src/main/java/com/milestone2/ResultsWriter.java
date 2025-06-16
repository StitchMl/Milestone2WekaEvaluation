package com.milestone2;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * Writes aggregated results to the CSV file.
 * Supports append mode without duplicating the header.
 */
public class ResultsWriter implements AutoCloseable {
    private static final String[] HEADER = {
            "Dataset","Classifier","Accuracy","Precision","Recall","F1",
            "Kappa","AUC","NPofB20"
    };

    private final CSVPrinter printer;

    public ResultsWriter() throws IOException {
        File file = new File(Config.RESULTS_CSV);
        boolean append = file.exists() && file.length() > 0;
        Writer out = new FileWriter(file, append);
        CSVFormat fmt;
        if (append) {
            // Existing file: we do not rewrite the header
            fmt = CSVFormat.DEFAULT.builder()
                    .setHeader(HEADER)
                    .setSkipHeaderRecord(true)
                    .get();
        } else {
            // New file: writing the header
            fmt = CSVFormat.DEFAULT.builder()
                    .setHeader(HEADER)
                    .get();
        }
        printer = new CSVPrinter(out, fmt);
    }

    /**
     * Adds a row of CSV (aggregated) results.
     *
     * @param ds Name of the dataset
     * @param clf Name of the classifier
     * @param m Map (metric -> mean value)
     */
    public void write(String ds, String clf, Map<String,Double> m) throws IOException {
        printer.printRecord(ds, clf,
                m.get("Accuracy"), m.get("Precision"), m.get("Recall"), m.get("F1"),
                m.get("Kappa"), m.get("AUC"), m.get("NPofB20"));
    }

    @Override
    public void close() throws IOException {
        printer.close();
    }
}