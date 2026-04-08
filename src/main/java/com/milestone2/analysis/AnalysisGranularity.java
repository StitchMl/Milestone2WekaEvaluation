package com.milestone2.analysis;

/**
 * Supported analysis granularities for repository-derived datasets.
 */
public enum AnalysisGranularity {
    CLASS,
    METHOD;

    public static AnalysisGranularity from(String raw) {
        if (raw == null || raw.isBlank()) {
            return Config.DEFAULT_GRANULARITY;
        }
        return AnalysisGranularity.valueOf(raw.trim().toUpperCase());
    }
}

