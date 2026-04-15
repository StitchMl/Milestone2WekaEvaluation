package com.milestone2;

import com.milestone2.analysis.*;
import com.milestone2.classifier.ClassifierCatalog;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class Milestone2SmokeTest {

    @Test
    void milestone2RunProducesExpectedOutputsWithRealWekaPipeline() throws Exception {
        Path tempRoot = Files.createTempDirectory("milestone2-smoke");
        Path dataDir = Files.createDirectory(tempRoot.resolve("data"));
        Path outputDir = Files.createDirectory(tempRoot.resolve("out"));
        Path dataset = dataDir.resolve("demo.arff");
        Files.writeString(dataset, demoDataset(), StandardCharsets.UTF_8);

        AnalysisConfig config = AnalysisConfig.fromArgs(new String[]{
                "--data-dir=" + dataDir,
                "--output-dir=" + outputDir,
                "--classifier-config=classifiers.properties",
                "--class-attribute=bug",
                "--positive-class=yes",
                "--seed=123",
                "--smote=false"
        });

        try {
            AnalysisRuntime runtime = new AnalysisRuntime();
            runtime.prepare(config);

            ClassifierCatalog classifierCatalog = ClassifierCatalog.load(
                    config.getPaths().getClassifierConfigPath(),
                    config.getSelection().getClassifierIds()
            );
            new AnalysisStartupValidator().validate(config, classifierCatalog);

            try (AnalysisOutputs outputs = AnalysisOutputs.open(config)) {
                new AnalysisRunner().run(config, classifierCatalog, outputs);
            }

            assertTrue(Files.exists(config.getPaths().getResultsCsv()));
            assertTrue(Files.exists(config.getPaths().getFoldCsv()));
            assertTrue(Files.exists(config.getPaths().getMilestone2SummaryCsv()));
            assertTrue(Files.exists(config.getPaths().getFeatureCorrelationsCsv()));
            assertTrue(Files.exists(config.getPaths().getWhatIfSummaryCsv()));
            assertTrue(Files.size(config.getPaths().getResultsCsv()) > 0);
            assertTrue(Files.size(config.getPaths().getFoldCsv()) > 0);
            assertTrue(Files.size(config.getPaths().getMilestone2SummaryCsv()) > 0);
            assertTrue(Files.size(config.getPaths().getFeatureCorrelationsCsv()) > 0);
            assertTrue(Files.size(config.getPaths().getWhatIfSummaryCsv()) > 0);

            String results = Files.readString(config.getPaths().getResultsCsv(), StandardCharsets.UTF_8);
            String foldMetrics = Files.readString(config.getPaths().getFoldCsv(), StandardCharsets.UTF_8);
            String summary = Files.readString(config.getPaths().getMilestone2SummaryCsv(), StandardCharsets.UTF_8);
            String correlations = Files.readString(config.getPaths().getFeatureCorrelationsCsv(), StandardCharsets.UTF_8);
            String whatIf = Files.readString(config.getPaths().getWhatIfSummaryCsv(), StandardCharsets.UTF_8);
            assertTrue(results.contains("Random Forest"));
            assertTrue(results.contains("Naive Bayes"));
            assertTrue(results.contains("K-Nearest Neighbors"));
            assertTrue(results.contains("walk-forward"));
            assertTrue(foldMetrics.contains("r1..r3") || foldMetrics.contains("r1..r2"));
            assertTrue(summary.contains("OVERALL_WINNER"));
            assertTrue(summary.contains("METRIC_WINNER"));
            assertTrue(correlations.contains("NSmells"));
            assertTrue(correlations.contains("SelectedForWhatIf"));
            assertTrue(whatIf.contains("B+->B"));
        } finally {
            deleteRecursively(tempRoot);
        }
    }

    @Test
    void milestone2RunSupportsCsvProjectDatasets() throws Exception {
        Path tempRoot = Files.createTempDirectory("milestone2-csv-smoke");
        Path dataDir = Files.createDirectory(tempRoot.resolve("data"));
        Path outputDir = Files.createDirectory(tempRoot.resolve("out"));
        Path dataset = dataDir.resolve("demo.csv");
        Files.writeString(dataset, demoCsvDataset(), StandardCharsets.UTF_8);

        AnalysisConfig config = AnalysisConfig.fromArgs(new String[]{
                "--data-dir=" + dataDir,
                "--output-dir=" + outputDir,
                "--classifier-config=classifiers.properties",
                "--class-attribute=bug",
                "--positive-class=yes",
                "--validation=cross-validation",
                "--runs=2",
                "--folds=2",
                "--threads=1",
                "--whatif=false"
        });

        try {
            AnalysisRuntime runtime = new AnalysisRuntime();
            runtime.prepare(config);

            ClassifierCatalog classifierCatalog = ClassifierCatalog.load(
                    config.getPaths().getClassifierConfigPath(),
                    config.getSelection().getClassifierIds()
            );
            new AnalysisStartupValidator().validate(config, classifierCatalog);

            try (AnalysisOutputs outputs = AnalysisOutputs.open(config)) {
                new AnalysisRunner().run(config, classifierCatalog, outputs);
            }

            assertTrue(Files.exists(config.getPaths().getResultsCsv()));
            assertTrue(Files.exists(config.getPaths().getFoldCsv()));
            assertTrue(Files.exists(config.getPaths().getMilestone2SummaryCsv()));
        } finally {
            deleteRecursively(tempRoot);
        }
    }

    private String demoDataset() {
        return String.join(System.lineSeparator(),
                "@relation demo",
                "@attribute ReleaseId {r1,r2,r3,r4}",
                "@attribute LOC numeric",
                "@attribute WMC numeric",
                "@attribute NSmells numeric",
                "@attribute RFC numeric",
                "@attribute bug {yes,no}",
                "@data",
                "r1,10,1,0,4,no",
                "r1,12,1,0,5,no",
                "r1,14,2,1,6,no",
                "r1,18,3,2,7,yes",
                "r1,20,4,3,8,yes",
                "r2,11,1,0,4,no",
                "r2,13,2,1,5,no",
                "r2,17,3,2,7,yes",
                "r2,19,4,3,8,yes",
                "r2,22,5,4,9,yes",
                "r3,12,1,0,4,no",
                "r3,15,2,1,5,no",
                "r3,18,3,2,7,yes",
                "r3,21,4,4,8,yes",
                "r3,24,5,5,10,yes",
                "r4,13,1,0,4,no",
                "r4,16,2,1,5,no",
                "r4,20,3,3,7,yes",
                "r4,23,4,4,9,yes",
                "r4,26,6,6,11,yes"
        );
    }

    private String demoCsvDataset() {
        return String.join(System.lineSeparator(),
                "ReleaseId,LOC,WMC,NSmells,RFC,bug",
                "r1,10,1,0,4,no",
                "r1,12,1,0,5,no",
                "r1,14,2,1,6,no",
                "r1,18,3,2,7,yes",
                "r1,20,4,3,8,yes",
                "r2,11,1,0,4,no",
                "r2,13,2,1,5,no",
                "r2,17,3,2,7,yes",
                "r2,19,4,3,8,yes",
                "r2,22,5,4,9,yes",
                "r3,12,1,0,4,no",
                "r3,15,2,1,5,no",
                "r3,18,3,2,7,yes",
                "r3,21,4,4,8,yes",
                "r3,24,5,5,10,yes",
                "r4,13,1,0,4,no",
                "r4,16,2,1,5,no",
                "r4,20,3,3,7,yes",
                "r4,23,4,4,9,yes",
                "r4,26,6,6,11,yes"
        );
    }

    @SuppressWarnings("resource")
    private void deleteRecursively(Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }
        Files.walk(path)
                .sorted((left, right) -> right.getNameCount() - left.getNameCount())
                .forEach(current -> {
                    try {
                        Files.deleteIfExists(current);
                    } catch (IOException ignored) {
                        current.toFile().deleteOnExit();
                    }
                });
    }
}
