package com.milestone2;

import weka.core.Instances;
import weka.core.converters.CSVLoader;

import java.io.File;
import java.io.IOException;

public class CsvDataLoader {
    public Instances load(String filename) throws IOException {
        CSVLoader loader = new CSVLoader();
        loader.setSource(new File(Config.DATA_DIR + filename));
        Instances data = loader.getDataSet();
        data.setClassIndex(data.numAttributes() - 1);
        return data;
    }
}