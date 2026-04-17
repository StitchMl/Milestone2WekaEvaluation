package com.milestone2.classifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses comma-separated classifier identifiers while preserving declaration order.
 */
public class ClassifierIdParser {
    /**
     * Parses the raw comma-separated list of classifier identifiers.
     *
     * @param raw raw CLI value
     * @return ordered classifier identifiers without blank entries
     */
    public List<String> parse(String raw) {
        List<String> ids = new ArrayList<>();
        if (raw == null || raw.isBlank()) {
            return ids;
        }

        for (String token : raw.split(",")) {
            String id = token.trim();
            if (!id.isEmpty()) {
                ids.add(id);
            }
        }
        return ids;
    }
}

