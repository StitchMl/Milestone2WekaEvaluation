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

    public AnalysisPaths build() {
        return new AnalysisPaths(dataDir, outputDir, classifierConfigPath);
    }
}

