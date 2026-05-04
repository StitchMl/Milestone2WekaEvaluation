package com.milestone2.evaluation;

import java.util.Locale;

/**
 * Supported feature-selection strategies applied before balancing inside each training fold.
 */
public enum FeatureSelectionStrategy {
 NONE("none"),
 FILTER("filter"),
 WRAPPER("wrapper");

 private final String cliValue;

 FeatureSelectionStrategy(String cliValue) {
 this.cliValue = cliValue;
 }

 /**
 * Parses a CLI value into a feature-selection strategy.
 *
 * @param raw raw CLI value such as {@code filter} or {@code wrapper}
 * @return parsed strategy
 */
 public static FeatureSelectionStrategy from(String raw) {
 if (raw == null || raw.isBlank()) {
 throw new IllegalArgumentException("Feature-selection strategy cannot be blank");
 }
 String normalized = raw.trim().toLowerCase(Locale.ROOT);
 for (FeatureSelectionStrategy strategy : values()) {
 if (strategy.cliValue.equals(normalized)) {
 return strategy;
 }
 }
 throw new IllegalArgumentException("Unsupported feature-selection strategy: " + raw);
 }

 /**
 * Returns the stable CLI-friendly value.
 *
 * @return CLI value for the strategy
 */
 public String getCliValue() {
 return cliValue;
 }
}
