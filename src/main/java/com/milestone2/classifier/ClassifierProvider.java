package com.milestone2.classifier;

import weka.classifiers.Classifier;

public interface ClassifierProvider {
    String name();
    Classifier create();
}