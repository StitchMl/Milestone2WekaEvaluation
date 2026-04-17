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

    /**
     * Returns dataset A, namely the original dataset.
     *
     * @return original dataset
     */
    public Instances getOriginalDataset() {
        return originalDataset;
    }

    /**
     * Returns dataset B+, containing instances where the selected feature is strictly positive.
     *
     * @return B+ dataset
     */
    public Instances getBPlusDataset() {
        return bPlusDataset;
    }

    /**
     * Returns dataset B, derived from B+ by forcing the selected feature to zero.
     *
     * @return B dataset
     */
    public Instances getBDataset() {
        return bDataset;
    }

    /**
     * Returns dataset C, containing instances where the selected feature is already zero.
     *
     * @return C dataset
     */
    public Instances getCDataset() {
        return cDataset;
    }
}

