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
    public List<Path> list(Path dataDir) throws Exception {
        try (Stream<Path> stream = Files.list(dataDir)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(this::isSupportedDataset)
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .collect(Collectors.toList());
        }
    }

    private boolean isSupportedDataset(Path path) {
        String fileName = path.getFileName().toString().toLowerCase(Locale.ROOT);
        return fileName.endsWith(".csv") || fileName.endsWith(".arff");
    }
}

