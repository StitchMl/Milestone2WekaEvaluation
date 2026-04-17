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

    /**
     * Parses one raw CLI token expressed in {@code --key=value} form.
     *
     * @param raw raw CLI token
     * @return parsed argument
     */
    public static CliArgument parse(String raw) {
        if (!raw.startsWith("--")) {
            throw new IllegalArgumentException("Unsupported argument: " + raw);
        }

        String[] parts = raw.substring(2).split("=", 2);
        String value = parts.length > 1 ? parts[1] : "";
        return new CliArgument(parts[0], value);
    }

    /**
     * Returns the argument key without the leading {@code --}.
     *
     * @return argument key
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the raw argument value, or an empty string when none was provided.
     *
     * @return argument value
     */
    public String getValue() {
        return value;
    }
}

