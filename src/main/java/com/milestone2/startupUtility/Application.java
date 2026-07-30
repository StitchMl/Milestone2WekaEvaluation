package com.milestone2.startupUtility;

import com.milestone2.classifier.Catalog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bootstraps and runs a full analysis execution.
 */
public class Application {
 private static final Logger log = LoggerFactory.getLogger(Application.class);

 private final RuntimePreparer analysisRuntime;
 private final StartupValidator analysisStartupValidator;
 private final Runner analysisRunner;

 public Application() {
 this(new RuntimePreparer(), new StartupValidator(), new Runner());
 }

 Application(RuntimePreparer analysisRuntime,
 StartupValidator analysisStartupValidator,
 Runner analysisRunner) {
 this.analysisRuntime = analysisRuntime;
 this.analysisStartupValidator = analysisStartupValidator;
 this.analysisRunner = analysisRunner;
 }

 /**
 * Parses CLI arguments, validates startup prerequisites and executes the full analysis workflow.
 *
 * @param args CLI arguments in {@code --key=value} form
 */
 public void run(String[] args) {
 try {
 RunConfig config = RunConfig.fromArgs(args);
 ResolvedPaths paths = config.getPaths();
 SelectionSettings selection = config.getSelection();
 ExecutionSettings execution = config.getExecution();
 analysisRuntime.prepare(config);

 log.info("Analysis run '{}' started with granularity={} dataDir='{}' strategy={} temporalAttribute='{}' seed={}",
 execution.getRunId(),
 selection.getGranularity(),
 paths.getDataDir(),
 execution.getValidationStrategy().getCliValue(),
 execution.getTemporalAttributeName(),
 execution.getSeed());

 Catalog classifierCatalog = Catalog.load(
 paths.getClassifierConfigPath(),
 selection.getClassifierIds()
 );
 analysisStartupValidator.validate(config, classifierCatalog);

 try (OutputWriters outputs = OutputWriters.open(config)) {
 analysisRunner.run(config, classifierCatalog, outputs);
 }

 log.info("Analysis run '{}' completed successfully", execution.getRunId());
 } catch (Exception exception) {
 log.error("Fatal error while running analysis", exception);
 System.exit(1);
 }
 }
}

