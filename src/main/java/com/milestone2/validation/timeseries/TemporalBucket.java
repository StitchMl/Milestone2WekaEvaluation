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

    /**
     * Returns the temporal label that identifies the bucket.
     *
     * @return bucket label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns the instances belonging to the temporal bucket.
     *
     * @return bucket instances
     */
    public Instances getInstances() {
        return instances;
    }
}
