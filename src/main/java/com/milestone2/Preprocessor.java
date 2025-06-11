package com.milestone2;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.RemoveType;
import weka.filters.unsupervised.attribute.NominalToBinary;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import weka.filters.unsupervised.attribute.Standardize;

public class Preprocessor {
    /**
     * 1) Removes string attributes
     * 2) Converts multivalued nominals to binaries
     * 3) Impute missing and standardize
     */
    public Instances applyFilters(Instances data) throws Exception {
        // Removes strings (ID, path, etc.)
        RemoveType removeStrings = new RemoveType();
        removeStrings.setOptions(new String[]{ "-T", "string" });
        removeStrings.setInputFormat(data);
        Instances noStrings = Filter.useFilter(data, removeStrings);

        // Converts nominal to dummy variables
        NominalToBinary ntb = new NominalToBinary();
        ntb.setInputFormat(noStrings);
        Instances binary = Filter.useFilter(noStrings, ntb);

        // Imputation of missing values
        ReplaceMissingValues rmv = new ReplaceMissingValues();
        rmv.setInputFormat(binary);
        Instances noMissing = Filter.useFilter(binary, rmv);

        // Standardization of numerics
        Standardize std = new Standardize();
        std.setInputFormat(noMissing);
        return Filter.useFilter(noMissing, std);
    }
}