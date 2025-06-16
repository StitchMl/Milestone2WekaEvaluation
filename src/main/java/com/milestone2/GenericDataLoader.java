package com.milestone2;

import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.core.converters.ArffLoader;

import java.io.File;
import java.io.IOException;

/**
 * Loads dataset from CSV or ARFF file according to extension.
 */
public class GenericDataLoader {
    /**
     * Loads a dataset from the specified file (CSV or ARFF).
     *
     * @param filename Name of the file (in the Config.DATA_DIR folder)
     * @return Instances of the loaded dataset
     * @throws IOException If the file is not found or there is a read error
     */
    public Instances load(String filename) throws IOException {
        String path = Config.DATA_DIR + filename;
        if (filename.toLowerCase().endsWith(".csv")) {
            CSVLoader loader = new CSVLoader();
            loader.setSource(new File(path));
            Instances data = loader.getDataSet();
            data.setClassIndex(data.numAttributes() - 1);
            return data;
        } else if (filename.toLowerCase().endsWith(".arff")) {
            ArffLoader loader = new ArffLoader();
            loader.setSource(new File(path));
            Instances data = loader.getDataSet();
            data.setClassIndex(data.numAttributes() - 1);
            return data;
        } else {
            throw new IOException("Unsupported file format: " + filename);
        }
    }
}