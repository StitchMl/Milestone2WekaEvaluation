package com.milestone2.evaluation;

import com.milestone2.startupUtility.RunConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.Classifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.SMOTE;
import weka.filters.supervised.instance.SpreadSubsample;
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

 /**
 * Builds the fold-local preprocessing pipeline to wrap around the classifier being evaluated.
 *
 * @param baseClassifier classifier to place at the end of the pipeline
 * @param config immutable analysis configuration
 * @return configured filtered classifier
 * @throws Exception when the filter chain cannot be built
 */
 public FilteredClassifier buildPipeline(Classifier baseClassifier, RunConfig config) throws Exception {
 FilteredClassifier pipeline = new FilteredClassifier();
 pipeline.setFilter(buildFilterChain(config));
 pipeline.setClassifier(baseClassifier);
 return pipeline;
 }

 /**
 * Creates the ordered filter chain that removes unsupported attributes, imputes values, normalizes features,
 * optionally applies feature selection (FILTER or WRAPPER) before balancing, and optionally applies a balancing
 * filter (SMOTE, undersampling, or oversampling) on the training set only.
 *
 * @param config immutable analysis configuration
 * @return configured multi-filter chain
 * @throws Exception when a filter cannot be configured
 */
 private Filter buildFilterChain(RunConfig config) throws Exception {
 List<Filter> filters = new ArrayList<>();

 RemoveType removeStringFilter = new RemoveType();
 removeStringFilter.setOptions(new String[]{"-T", "string"});
 filters.add(removeStringFilter);
 filters.add(new NominalToBinary());
 filters.add(new ReplaceMissingValues());
 filters.add(new Standardize());

 // Feature selection is applied BEFORE balancing so it selects on the original class distribution
 switch (config.getExecution().getFeatureSelectionStrategy()) {
 case FILTER: {
 AttributeSelection fs = new AttributeSelection();
 fs.setEvaluator(new InfoGainAttributeEval());
 Ranker ranker = new Ranker();
 ranker.setThreshold(0.0); // keep all attributes with positive gain
 fs.setSearch(ranker);
 filters.add(fs);
 break;
 }
 case WRAPPER: {
 AttributeSelection fs = new AttributeSelection();
 fs.setEvaluator(new CfsSubsetEval());
 fs.setSearch(new BestFirst());
 filters.add(fs);
 break;
 }
 default:
 break;
 }

 switch (config.getExecution().getBalancingStrategy()) {
 case SMOTE:
 filters.add(new SMOTE());
 break;
 case UNDERSAMPLING:
 SpreadSubsample sub = new SpreadSubsample();
 sub.setOptions(new String[]{"-M", "1.0"});
 filters.add(sub);
 break;
 case OVERSAMPLING:
 Resample resample = new Resample();
 resample.setOptions(new String[]{"-B", "1.0", "-Z", "200"});
 filters.add(resample);
 break;
 default:
 break;
 }

 MultiFilter filterChain = new MultiFilter();
 filterChain.setFilters(filters.toArray(new Filter[0]));
 log.debug("Preprocessing pipeline created with {} filters", filters.size());
 return filterChain;
 }
}

