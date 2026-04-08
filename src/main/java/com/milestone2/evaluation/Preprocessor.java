package com.milestone2.evaluation;

import com.milestone2.analysis.AnalysisConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Classifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.supervised.instance.SMOTE;
import weka.filters.unsupervised.attribute.NominalToBinary;
import weka.filters.unsupervised.attribute.RemoveType;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import weka.filters.unsupervised.attribute.Standardize;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds the preprocessing pipeline that is fitted inside each training fold.
 */
public class Preprocessor {
    private static final Logger log = LoggerFactory.getLogger(Preprocessor.class);

    public FilteredClassifier buildPipeline(Classifier baseClassifier, AnalysisConfig config) throws Exception {
        FilteredClassifier pipeline = new FilteredClassifier();
        pipeline.setFilter(buildFilterChain(config));
        pipeline.setClassifier(baseClassifier);
        return pipeline;
    }

    private Filter buildFilterChain(AnalysisConfig config) throws Exception {
        List<Filter> filters = new ArrayList<>();

        RemoveType removeStringFilter = new RemoveType();
        removeStringFilter.setOptions(new String[]{"-T", "string"});
        filters.add(removeStringFilter);
        filters.add(new NominalToBinary());
        filters.add(new ReplaceMissingValues());
        filters.add(new Standardize());

        if (config.getExecution().isApplySmote()) {
            filters.add(new SMOTE());
        }

        MultiFilter filterChain = new MultiFilter();
        filterChain.setFilters(filters.toArray(new Filter[0]));
        log.debug("Preprocessing pipeline created with {} filters", filters.size());
        return filterChain;
    }
}


