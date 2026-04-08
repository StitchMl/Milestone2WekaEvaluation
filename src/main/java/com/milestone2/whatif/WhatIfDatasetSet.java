package com.milestone2.whatif;

import weka.core.Instances;

/**
 * Scenario datasets derived from the original dataset.
 */
public class WhatIfDatasetSet {
    private final Instances originalDataset;
    private final Instances bPlusDataset;
    private final Instances bDataset;
    private final Instances cDataset;

    public WhatIfDatasetSet(Instances originalDataset,
                            Instances bPlusDataset,
                            Instances bDataset,
                            Instances cDataset) {
        this.originalDataset = originalDataset;
        this.bPlusDataset = bPlusDataset;
        this.bDataset = bDataset;
        this.cDataset = cDataset;
    }

    public Instances getOriginalDataset() {
        return originalDataset;
    }

    public Instances getBPlusDataset() {
        return bPlusDataset;
    }

    public Instances getBDataset() {
        return bDataset;
    }

    public Instances getCDataset() {
        return cDataset;
    }
}

