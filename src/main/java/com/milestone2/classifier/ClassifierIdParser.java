package com.milestone2.classifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses comma-separated classifier identifiers while preserving declaration order.
 */
public class ClassifierIdParser {
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

