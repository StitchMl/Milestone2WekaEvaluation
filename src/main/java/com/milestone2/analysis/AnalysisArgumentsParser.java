package com.milestone2.analysis;

/**
 * Parses CLI arguments into a validated {@link AnalysisConfig}.
 */
public class AnalysisArgumentsParser {
    public AnalysisConfig parse(String[] args) {
        AnalysisConfigBuilder builder = new AnalysisConfigBuilder();
        for (String arg : args) {
            builder.apply(CliArgument.parse(arg));
        }
        return builder.build();
    }
}

