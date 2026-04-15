package com.milestone2.analysis;

import com.milestone2.classifier.ClassifierCatalog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bootstraps and runs a full analysis execution.
 */
public class AnalysisApplication {
    private static final Logger log = LoggerFactory.getLogger(AnalysisApplication.class);

    private final AnalysisRuntime analysisRuntime;
    private final AnalysisStartupValidator analysisStartupValidator;
    private final AnalysisRunner analysisRunner;

    public AnalysisApplication() {
        this(new AnalysisRuntime(), new AnalysisStartupValidator(), new AnalysisRunner());
    }

    AnalysisApplication(AnalysisRuntime analysisRuntime,
                        AnalysisStartupValidator analysisStartupValidator,
                        AnalysisRunner analysisRunner) {
        this.analysisRuntime = analysisRuntime;
        this.analysisStartupValidator = analysisStartupValidator;
        this.analysisRunner = analysisRunner;
    }

    public void run(String[] args) {
        try {
            AnalysisConfig config = AnalysisConfig.fromArgs(args);
            AnalysisPaths paths = config.getPaths();
            AnalysisSelection selection = config.getSelection();
            AnalysisExecution execution = config.getExecution();
            analysisRuntime.prepare(config);

            log.info("Analysis run '{}' started with granularity={} dataDir='{}' strategy={} temporalAttribute='{}' seed={}",
                    execution.getRunId(),
                    selection.getGranularity(),
                    paths.getDataDir(),
                    execution.getValidationStrategy().getCliValue(),
                    execution.getTemporalAttributeName(),
                    execution.getSeed());

            ClassifierCatalog classifierCatalog = ClassifierCatalog.load(
                    paths.getClassifierConfigPath(),
                    selection.getClassifierIds()
            );
            analysisStartupValidator.validate(config, classifierCatalog);

            try (AnalysisOutputs outputs = AnalysisOutputs.open(config)) {
                analysisRunner.run(config, classifierCatalog, outputs);
            }

            log.info("Analysis run '{}' completed successfully", execution.getRunId());
        } catch (Exception exception) {
            log.error("Fatal error while running analysis", exception);
            System.exit(1);
        }
    }
}

