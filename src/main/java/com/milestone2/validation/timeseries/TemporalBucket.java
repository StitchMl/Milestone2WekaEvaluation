package com.milestone2.validation.timeseries;

import weka.core.Instances;

/**
 * Consecutive dataset slice that belongs to one temporal period.
 */
public class TemporalBucket {
    private final String label;
    private final Instances instances;

    public TemporalBucket(String label, Instances instances) {
        this.label = label;
        this.instances = instances;
    }

    public String getLabel() {
        return label;
    }

    public Instances getInstances() {
        return instances;
    }
}
