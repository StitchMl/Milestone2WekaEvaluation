package com.milestone2;

import com.milestone2.analysis.AnalysisConfig;
import com.milestone2.analysis.Config;
import com.milestone2.dataset.GenericDataLoader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import weka.core.Instances;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GenericDataLoaderTest {

    private final List<Path> filesToDelete = new ArrayList<>();

    @AfterEach
    void cleanUp() throws IOException {
        for (Path path : filesToDelete) {
            deleteEventually(path);
        }
    }

    @Test
    void loadCsvSetsTheLastAttributeAsClass() throws Exception {
        Path csv = createDataFile(
                ".csv",
                "metricA,LOC,bug%n1,10,yes%n2,20,no%n"
        );

        Instances data = new GenericDataLoader().load(csv, AnalysisConfig.fromArgs(new String[0]));

        assertEquals(2, data.numInstances());
        assertEquals(3, data.numAttributes());
        assertEquals(2, data.classIndex());
        assertEquals("bug", data.classAttribute().name());
    }

    @Test
    void loadArffSetsTheLastAttributeAsClass() throws Exception {
        Path arff = createDataFile(
                ".arff",
                "@relation demo%n" +
                        "@attribute metricA numeric%n" +
                        "@attribute LOC numeric%n" +
                        "@attribute bug {yes,no}%n" +
                        "@data%n" +
                        "1,10,yes%n" +
                        "2,20,no%n"
        );

        Instances data = new GenericDataLoader().load(arff, AnalysisConfig.fromArgs(new String[0]));

        assertEquals(2, data.numInstances());
        assertEquals(3, data.numAttributes());
        assertEquals(2, data.classIndex());
        assertEquals("bug", data.classAttribute().name());
    }

    @Test
    void loadRejectsUnsupportedFormats() {
        assertThrows(IOException.class, () -> new GenericDataLoader().load(
                Paths.get("demo.txt"),
                AnalysisConfig.fromArgs(new String[0])
        ));
    }

    @Test
    void loadUsesConfiguredClassAttributeWhenProvided() throws Exception {
        Path csv = createDataFile(
                ".csv",
                "bug,metricA,LOC%nyes,1,10%nno,2,20%n"
        );

        AnalysisConfig config = AnalysisConfig.fromArgs(new String[]{"--class-attribute=bug"});
        Instances data = new GenericDataLoader().load(csv, config);

        assertEquals(0, data.classIndex());
        assertEquals("bug", data.classAttribute().name());
    }

    private Path createDataFile(String extension, String content) throws IOException {
        Path dataDir = Paths.get(Config.DATA_DIR);
        Files.createDirectories(dataDir);

        Path file = dataDir.resolve("test-" + UUID.randomUUID() + extension);
        Files.writeString(file, String.format(content), StandardCharsets.UTF_8);
        filesToDelete.add(file);
        return file;
    }

    private void deleteEventually(Path path) throws IOException {
        for (int attempt = 0; attempt < 5; attempt++) {
            try {
                Files.deleteIfExists(path);
                return;
            } catch (FileSystemException ex) {
                if (attempt == 4) {
                    path.toFile().deleteOnExit();
                    return;
                }
                System.gc();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Interrupted while cleaning test files", interrupted);
                }
            }
        }
    }
}

