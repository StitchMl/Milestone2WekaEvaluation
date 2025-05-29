package com.milestone2.io;

import com.milestone2.evaluation.CVResult;
import java.io.Closeable;
import java.io.IOException;

public interface ResultWriter extends Closeable {

    /**
     * Writes the header of the result file.
     * This method should be called before writing any results.
     *
     * @throws IOException if an I/O error occurs
     */
    void writeHeader() throws IOException;

    /**
     * Writes the result of a cross-validation run.
     *
     * @param datasetPath the path to the dataset used for the cross-validation
     * @param classifierName the name of the classifier used
     * @param result the result of the cross-validation
     * @throws IOException if an I/O error occurs
     */
    void writeResult(String datasetPath, String classifierName, CVResult result) throws IOException;
}