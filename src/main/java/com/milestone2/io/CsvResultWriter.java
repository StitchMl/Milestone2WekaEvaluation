package com.milestone2.io;

import com.milestone2.evaluation.CVResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.Locale;

public class CsvResultWriter implements ResultWriter {
    private static final Logger logger = LoggerFactory.getLogger(CsvResultWriter.class);
    private final PrintWriter out;

    public CsvResultWriter(String filename) throws IOException {
        Path path = Paths.get(filename);
        out = new PrintWriter(Files.newBufferedWriter(path));
    }

    @Override
    public void writeHeader() {
        out.println("Dataset,Classifier,Precision,Recall,AUC,Kappa,NPofB20");
        out.flush();
    }

    @Override
    public void writeResult(String dataset, String clsName, CVResult r) {
        // Usa Locale.US per avere il punto decimale
        out.printf(Locale.US,
                "%s,%s,%.4f,%.4f,%.4f,%.4f,%.4f%n",
                escape(dataset),
                clsName,
                r.precision,
                r.recall,
                r.auc,
                r.kappa,
                r.npOfB20);
        out.flush();
    }

    private String escape(String s) {
        if (s.contains(",") || s.contains("\"")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    @Override
    public void close() {
        out.close();
        logger.info("Successfully written results");
    }
}