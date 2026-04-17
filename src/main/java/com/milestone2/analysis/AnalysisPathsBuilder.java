package com.milestone2.analysis;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Collects CLI values related to filesystem locations.
 */
public class AnalysisPathsBuilder {
    private Path dataDir = Paths.get(Config.DATA_DIR);
    private Path outputDir = Paths.get(Config.OUTPUT_DIR);
    private Path classifierConfigPath = Paths.get(Config.CLASSIFIERS_CONFIG);

    /**
     * Applies one filesystem-related CLI argument.
     *
     * @param argument parsed CLI argument
     * @return {@code true} when the argument belongs to this builder, {@code false} otherwise
     */
    public boolean apply(CliArgument argument) {
        switch (argument.getKey()) {
            case "data-dir":
                dataDir = Paths.get(argument.getValue());
                return true;
            case "output-dir":
                outputDir = Paths.get(argument.getValue());
                return true;
            case "classifier-config":
                classifierConfigPath = Paths.get(argument.getValue());
                return true;
            default:
                return false;
        }
    }

    /**
     * Creates the immutable path bundle for the current run.
     *
     * @return resolved analysis paths
     */
    public AnalysisPaths build() {
        return new AnalysisPaths(dataDir, outputDir, classifierConfigPath);
    }
}
