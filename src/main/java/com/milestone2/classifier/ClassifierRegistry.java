package com.milestone2.classifier;

import com.milestone2.config.ConfigLoader;
import java.util.*;

public class ClassifierRegistry {
    private ClassifierRegistry() {
        // Private builder to hide the implicit public one
    }

    public static List<ClassifierProvider> loadProviders(ConfigLoader config) {
        List<ClassifierProvider> list = new ArrayList<>();
        list.add(new RandomForestProvider(config));
        list.add(new NaiveBayesProvider());
        list.add(new IBkProvider(config));
        return list;
    }
}