package com.milestone2.io;

import com.milestone2.evaluation.CVResult;
import java.io.Closeable;
import java.io.IOException;

public interface ResultWriter extends Closeable {
    void writeHeader() throws IOException;
    void writeResult(String datasetPath, String classifierName, CVResult result) throws IOException;
}