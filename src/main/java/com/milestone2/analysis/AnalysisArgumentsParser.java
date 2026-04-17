package com.milestone2.analysis;

/**
 * Parses CLI arguments into a validated {@link AnalysisConfig}.
 */
public class AnalysisArgumentsParser {
    /**
     * Converts raw CLI arguments into the immutable configuration used by the application.
     *
     * @param args CLI arguments in {@code --key=value} form
     * @return validated analysis configuration
     */
    public AnalysisConfig parse(String[] args) {
        AnalysisConfigBuilder builder = new AnalysisConfigBuilder();
        for (String arg : args) {
            builder.apply(CliArgument.parse(arg));
        }
        return builder.build();
    }
}
