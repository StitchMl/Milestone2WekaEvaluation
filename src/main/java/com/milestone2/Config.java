package com.milestone2;

public class Config {

    private Config() {
        // Prevent instantiation
    }

    public static final String DATA_DIR = "src/main/resources/data/";
    public static final String OUTPUT_DIR = "output/";
    public static final String RESULTS_CSV = OUTPUT_DIR + "results.csv";
    public static final String CHARTS_DIR = OUTPUT_DIR + "charts/";
}