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

    /**
     * Constructor that initializes the writer to the specified CSV file.
     *
     * @param filename the name of the file to write results to
     * @throws IOException if an I/O error occurs opening the file
     */
    public CsvResultWriter(String filename) throws IOException {
        Path path = Paths.get(filename);
        out = new PrintWriter(Files.newBufferedWriter(path));
    }

    /**
     * Constructor that initializes the writer to the specified PrintWriter.
     */
    @Override
    public void writeHeader() {
        out.println("Dataset,Classifier,Precision,Recall,AUC,Kappa,NPofB20");
        out.flush();
    }

    /**
     * Writes the result of a cross-validation run to the CSV file.
     *
     * @param dataset  the name of the dataset
     * @param clsName  the name of the classifier
     * @param r        the CVResult containing evaluation metrics
     */
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

    /**
     * Escapes a string for CSV format.
     */
    private String escape(String s) {
        if (s.contains(",") || s.contains("\"")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    /**
     * Closes the writer and finalizes the CSV file.
     */
    @Override
    public void close() {
        out.close();
        logger.info("Successfully written results");
    }
}