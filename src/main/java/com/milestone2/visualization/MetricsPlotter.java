package com.milestone2.visualization;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class MetricsPlotter {
    private static final Logger logger = LoggerFactory.getLogger(MetricsPlotter.class);

    /**
     * Private constructor to avoid instantiation.
     */
    private MetricsPlotter() {
        // It does not have to be instantiated
    }

    /**
     * Loads the dataset from a CSV file and creates a box-and-whisker dataset.
     *
     * @param csvPath the path to the CSV file
     * @return a DefaultBoxAndWhiskerCategoryDataset
     */
    private static DefaultBoxAndWhiskerCategoryDataset loadDataset(Path csvPath) {
        Map<String, Map<String, List<Double>>> map = new LinkedHashMap<>();
        try (CSVReader reader = new CSVReader(new FileReader(csvPath.toFile()))) {
            String[] header = reader.readNext();
            int clsIdx = Arrays.asList(header).indexOf("Classifier");
            List<String> metrics = Arrays.asList("Precision","Recall","AUC","Kappa","NPofB20");
            Map<String,Integer> idx = new HashMap<>();
            for (int i = 0; i < header.length; i++) idx.put(header[i], i);

            String[] row;
            while ((row = reader.readNext()) != null) {
                String cls = row[clsIdx];
                map.putIfAbsent(cls, new LinkedHashMap<>());
                for (String m : metrics) {
                    map.get(cls)
                            .computeIfAbsent(m, k -> new ArrayList<>())
                            .add(Double.parseDouble(row[idx.get(m)]));
                }
            }
        } catch (IOException | CsvValidationException e) {
            logger.error("Error reading CSV file: {}", e.getMessage());
            return null;
        }

        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
        map.forEach((cls, mapp) ->
                mapp.forEach((metric, values) ->
                        dataset.add(values, cls, metric)
                )
        );
        return dataset;
    }

    /**
     * Creates a box plot from the CSV file and saves it as a PNG image.
     *
     * @param csvPath   the path to the CSV file
     * @param outputPng the path to save the PNG image
     */
    public static void createBoxPlot(Path csvPath, Path outputPng) throws IOException {
        logger.info("Generating box-plot from {}", csvPath);
        var dataset = loadDataset(csvPath);
        JFreeChart chart = ChartFactory.createBoxAndWhiskerChart(
                "Metric distribution by classifier",
                "Metrics","Value", dataset, true);
        ChartUtils.saveChartAsPNG(outputPng.toFile(), chart, 800, 600);
        logger.info("Box-plot saved in {}", outputPng);
    }
}