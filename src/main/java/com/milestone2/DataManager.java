package com.milestone2;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class DataManager {
    /** Load a dataset from ARFF or CSV file */
    public static Instances loadDataset(String path) throws Exception {
        DataSource source = new DataSource(path);
        Instances data = source.getDataSet();
        if (data.classIndex() == -1) {
            data.setClassIndex(data.numAttributes() - 1);
        }
        return data;
    }
}