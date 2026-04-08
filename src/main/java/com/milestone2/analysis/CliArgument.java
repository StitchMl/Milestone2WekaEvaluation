package com.milestone2.analysis;

/**
 * Immutable representation of a single CLI argument in --key=value form.
 */
public class CliArgument {
    private final String key;
    private final String value;

    private CliArgument(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static CliArgument parse(String raw) {
        if (!raw.startsWith("--")) {
            throw new IllegalArgumentException("Unsupported argument: " + raw);
        }

        String[] parts = raw.substring(2).split("=", 2);
        String value = parts.length > 1 ? parts[1] : "";
        return new CliArgument(parts[0], value);
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}


