package com.milestone2;

import com.milestone2.startupUtility.RunConfig;
import com.milestone2.startupUtility.Defaults;
import com.milestone2.dataset.DataLoader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import weka.core.Instances;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

 Instances data = new DataLoader().load(csv, RunConfig.fromArgs(new String[0]));

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

 Instances data = new DataLoader().load(arff, RunConfig.fromArgs(new String[0]));

 assertEquals(2, data.numInstances());
 assertEquals(3, data.numAttributes());
 assertEquals(2, data.classIndex());
 assertEquals("bug", data.classAttribute().name());
 }

 @Test
 void loadRejectsUnsupportedFormats() {
 assertThrows(IOException.class, () -> new DataLoader().load(
 Paths.get("demo.txt"),
 RunConfig.fromArgs(new String[0])
 ));
 }

 @Test
 void loadUsesConfiguredClassAttributeWhenProvided() throws Exception {
 Path csv = createDataFile(
 ".csv",
 "bug,metricA,LOC%nyes,1,10%nno,2,20%n"
 );

 RunConfig config = RunConfig.fromArgs(new String[]{"--class-attribute=bug"});
 Instances data = new DataLoader().load(csv, config);

 assertEquals(0, data.classIndex());
 assertEquals("bug", data.classAttribute().name());
 }

 private Path createDataFile(String extension, String content) throws IOException {
 Path dataDir = Paths.get(Defaults.DATA_DIR);
 Files.createDirectories(dataDir);

 Path file = dataDir.resolve("test-" + UUID.randomUUID() + extension);
 Files.writeString(file, String.format(content), StandardCharsets.UTF_8);
 filesToDelete.add(file);
 return file;
 }

 private void deleteEventually(Path path) throws IOException {
 try {
 Files.deleteIfExists(path);
 } catch (IOException ignored) {
 // File still locked by JVM (common on Windows); mark for deletion on JVM exit
 path.toFile().deleteOnExit();
 }
 }
}

