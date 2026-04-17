package com.milestone2.analysis;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Validates the filesystem inputs needed before starting an analysis run.
 */
public class AnalysisPathValidator {
    /**
     * Verifies that the configured dataset directory and classifier catalog exist and have the expected type.
     *
     * @param paths filesystem paths to validate
     */
    public void validate(AnalysisPaths paths) {
        validateDataDir(paths.getDataDir());
        validateClassifierConfig(paths.getClassifierConfigPath());
    }

    /**
     * Ensures that the dataset directory exists and is a directory.
     *
     * @param dataDir dataset directory to validate
     */
    private void validateDataDir(Path dataDir) {
        if (!Files.exists(dataDir)) {
            throw new IllegalArgumentException("Data directory does not exist: " + dataDir);
        }
        if (!Files.isDirectory(dataDir)) {
            throw new IllegalArgumentException("Data directory is not a directory: " + dataDir);
        }
    }

    /**
     * Ensures that the classifier configuration path exists and points to a regular file.
     *
     * @param classifierConfigPath classifier catalog path to validate
     */
    private void validateClassifierConfig(Path classifierConfigPath) {
        if (!Files.exists(classifierConfigPath)) {
            throw new IllegalArgumentException("Classifier configuration file does not exist: " + classifierConfigPath);
        }
        if (!Files.isRegularFile(classifierConfigPath)) {
            throw new IllegalArgumentException(
                    "Classifier configuration path is not a regular file: " + classifierConfigPath
            );
        }
    }
}

