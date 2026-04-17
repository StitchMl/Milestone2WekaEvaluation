package com.milestone2;

import com.milestone2.analysis.AnalysisApplication;

/**
 * Application entry point.
 */
public class MainApp {
    /**
     * Delegates startup to the analysis application.
     *
     * @param args CLI arguments in {@code --key=value} form
     */
    public static void main(String[] args) {
        new AnalysisApplication().run(args);
    }
}

