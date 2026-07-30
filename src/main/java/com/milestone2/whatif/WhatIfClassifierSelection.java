package com.milestone2.whatif;

import com.milestone2.classifier.Definition;

/**
 * Selected classifier for the what-if prediction scenarios.
 */
public class WhatIfClassifierSelection {
 private final Definition definition;
 private final String reason;

 public WhatIfClassifierSelection(Definition definition, String reason) {
 this.definition = definition;
 this.reason = reason;
 }

 /**
 * Returns the classifier selected for scenario prediction.
 *
 * @return selected classifier definition
 */
 public Definition getDefinition() {
 return definition;
 }

 /**
 * Returns the explanation of how the classifier was selected.
 *
 * @return selection reason
 */
 public String getReason() {
 return reason;
 }
}

