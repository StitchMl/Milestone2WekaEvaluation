package com.milestone2.analysis;

import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.LogManager;

/**
 * Prepares the runtime environment for an analysis execution.
 */
public class AnalysisRuntime {
    /**
     * Applies runtime prerequisites such as netlib configuration, logging tuning and output directory creation.
     *
     * @param config immutable analysis configuration
     * @throws Exception when runtime preparation fails
     */
    public void prepare(AnalysisConfig config) throws Exception {
        NetlibRuntimeConfigurer.configurePureJava();
        configureLogging();
        Files.createDirectories(config.getPaths().getOutputDir());
        Files.createDirectories(config.getPaths().getChartsDir());
    }

    /**
     * Reduces noisy java.util.logging output from native netlib discovery.
     */
    private void configureLogging() {
        LogManager.getLogManager().reset();
        java.util.logging.Logger.getLogger("com.github.fommil.netlib").setLevel(Level.SEVERE);
        java.util.logging.Logger.getLogger("com.github.fommil.jni").setLevel(Level.SEVERE);
    }
}

