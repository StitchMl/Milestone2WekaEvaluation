package com.milestone2.classifier;

import com.milestone2.config.ConfigLoader;
import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;

public class IBkProvider implements ClassifierProvider {
    private final int k;

    public IBkProvider(ConfigLoader config) {
        this.k = config.getIntProperty("ibk.k", 5);
    }

    @Override
    public String name() {
        return "IBk(k=" + k + ")";
    }

    @Override
    public Classifier create() {
        return new IBk(k);
    }
}