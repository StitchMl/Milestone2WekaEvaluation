package com.milestone2;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class ResultsWriter implements AutoCloseable {
    private static final String[] HEADER = {
            "Dataset","Classifier","Accuracy","Precision","Recall","F1",
            "Kappa","AUC","NPofB20"
    };

    private final CSVPrinter printer;

    public ResultsWriter() throws IOException {
        Writer out = new FileWriter(Config.RESULTS_CSV);
        CSVFormat fmt = CSVFormat.DEFAULT.builder()
                .setHeader(HEADER)
                .get();
        printer = new CSVPrinter(out, fmt);
    }

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