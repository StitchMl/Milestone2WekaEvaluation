package com.milestone2.classifier;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;

public class NaiveBayesProvider implements ClassifierProvider {
    @Override
    public String name() {
        return "NaiveBayes";
    }

    @Override
    public Classifier create() {
        return new NaiveBayes();
    }
}