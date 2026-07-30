package com.milestone2.startupUtility;

/**
 * Supported analysis granularities for repository-derived datasets.
 */
public enum Granularity {
 CLASS,
 METHOD;

 /**
 * Parses the CLI value for the analysis granularity, falling back to the configured default when blank.
 *
 * @param raw raw CLI value
 * @return parsed granularity
 */
 public static Granularity from(String raw) {
 if (raw == null || raw.isBlank()) {
 return Defaults.DEFAULT_GRANULARITY;
 }
 return Granularity.valueOf(raw.trim().toUpperCase());
 }
}
