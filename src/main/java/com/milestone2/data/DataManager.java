package com.milestone2.data;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class DataManager {
    private DataManager() {}

    /** Load ARFF or CSV dataset, set class as the last column if necessary */
    public static Instances loadDataset(String path) throws Exception {
        DataSource source = new DataSource(path);
        Instances data = source.getDataSet();
        if (data.classIndex() == -1) {
            data.setClassIndex(data.numAttributes() - 1);
        }
        return data;
    }
}