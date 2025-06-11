package com.milestone2;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import weka.filters.unsupervised.attribute.Standardize;

public class Preprocessor {
    public Instances applyFilters(Instances data) throws Exception {
        ReplaceMissingValues rmv = new ReplaceMissingValues();
        rmv.setInputFormat(data);
        Instances noMissing = Filter.useFilter(data, rmv);

        Standardize std = new Standardize();
        std.setInputFormat(noMissing);
        return Filter.useFilter(noMissing, std);
    }
}
