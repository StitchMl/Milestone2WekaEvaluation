package com.milestone2;

import com.milestone2.analysis.*;
import com.milestone2.classifier.ClassifierCatalog;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
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
                "--runs=2",
                "--folds=2",
                "--seed=123",
                "--smote=false",
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
            assertFalse(Files.exists(config.getPaths().getFeatureCorrelationsCsv()));
            assertFalse(Files.exists(config.getPaths().getWhatIfSummaryCsv()));
            assertTrue(Files.size(config.getPaths().getResultsCsv()) > 0);
            assertTrue(Files.size(config.getPaths().getFoldCsv()) > 0);
            assertTrue(Files.size(config.getPaths().getMilestone2SummaryCsv()) > 0);

            String results = Files.readString(config.getPaths().getResultsCsv(), StandardCharsets.UTF_8);
            String summary = Files.readString(config.getPaths().getMilestone2SummaryCsv(), StandardCharsets.UTF_8);
            assertTrue(results.contains("Random Forest"));
            assertTrue(results.contains("Naive Bayes"));
            assertTrue(results.contains("K-Nearest Neighbors"));
            assertTrue(summary.contains("OVERALL_WINNER"));
            assertTrue(summary.contains("METRIC_WINNER"));
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
                "@attribute LOC numeric",
                "@attribute WMC numeric",
                "@attribute RFC numeric",
                "@attribute bug {yes,no}",
                "@data",
                "10,1,4,no",
                "14,2,5,no",
                "18,3,7,yes",
                "22,4,8,yes",
                "11,1,4,no",
                "13,2,5,no",
                "19,3,7,yes",
                "24,5,9,yes"
        );
    }

    private String demoCsvDataset() {
        return String.join(System.lineSeparator(),
                "LOC,WMC,RFC,bug",
                "10,1,4,no",
                "14,2,5,no",
                "18,3,7,yes",
                "22,4,8,yes",
                "11,1,4,no",
                "13,2,5,no",
                "19,3,7,yes",
                "24,5,9,yes"
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

