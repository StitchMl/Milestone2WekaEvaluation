package com.milestone2.analysis;

import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.LogManager;

/**
 * Prepares the runtime environment for an analysis execution.
 */
public class AnalysisRuntime {
    public void prepare(AnalysisConfig config) throws Exception {
        configureLogging();
        Files.createDirectories(config.getPaths().getOutputDir());
        Files.createDirectories(config.getPaths().getChartsDir());
    }

    private void configureLogging() {
        LogManager.getLogManager().reset();
        java.util.logging.Logger.getLogger("com.github.fommil.netlib").setLevel(Level.SEVERE);
        java.util.logging.Logger.getLogger("com.github.fommil.jni").setLevel(Level.SEVERE);
    }
}

