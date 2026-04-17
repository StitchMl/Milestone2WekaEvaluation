package com.milestone2.analysis;

/**
 * Supported analysis granularities for repository-derived datasets.
 */
public enum AnalysisGranularity {
    CLASS,
    METHOD;

    /**
     * Parses the CLI value for the analysis granularity, falling back to the configured default when blank.
     *
     * @param raw raw CLI value
     * @return parsed granularity
     */
    public static AnalysisGranularity from(String raw) {
        if (raw == null || raw.isBlank()) {
            return Config.DEFAULT_GRANULARITY;
        }
        return AnalysisGranularity.valueOf(raw.trim().toUpperCase());
    }
}
