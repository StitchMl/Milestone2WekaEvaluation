package com.milestone2.startupUtility;

/**
 * Parses CLI arguments into a validated {@link RunConfig}.
 */
public class CliArgumentsParser {
 /**
 * Converts raw CLI arguments into the immutable configuration used by the application.
 *
 * @param args CLI arguments in {@code --key=value} form
 * @return validated analysis configuration
 */
 public RunConfig parse(String[] args) {
 RunConfigBuilder builder = new RunConfigBuilder();
 for (String arg : args) {
 builder.apply(CliArgument.parse(arg));
 }
 return builder.build();
 }
}
