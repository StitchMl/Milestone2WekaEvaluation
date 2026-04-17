package com.milestone2.dataset;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Discovers supported dataset files from a directory.
 */
public class DatasetDiscovery {
    /**
     * Lists supported dataset files from the configured directory in deterministic filename order.
     *
     * @param dataDir directory containing candidate datasets
     * @return ordered dataset paths
     * @throws Exception when the directory cannot be read
     */
    public List<Path> list(Path dataDir) throws Exception {
        try (Stream<Path> stream = Files.list(dataDir)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(this::isSupportedDataset)
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .collect(Collectors.toList());
        }
    }

    /**
     * Checks whether the file extension is supported by the generic dataset loader.
     *
     * @param path candidate file path
     * @return {@code true} for CSV and ARFF files
     */
    private boolean isSupportedDataset(Path path) {
        String fileName = path.getFileName().toString().toLowerCase(Locale.ROOT);
        return fileName.endsWith(".csv") || fileName.endsWith(".arff");
    }
}

