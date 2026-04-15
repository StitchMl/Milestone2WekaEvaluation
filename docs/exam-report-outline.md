# Exam Report Outline

This file is a compact writing scaffold aligned with the exam workflow supported by the codebase.

## 1. Introduction

- State the project goal and the target system under study.
- Explain that the workflow combines classifier benchmarking and a what-if analysis on a selected feature.

## 2. Why This Method

- Describe how the target method or entity was selected.
- Use `feature_correlations.csv` to discuss both the most correlated feature and the actionable smell feature selected for the what-if study.
- Explain why the chosen method belongs to the risky subset represented by `B+`.

## 3. Before The Change

- Describe the method before refactoring.
- Report the key metrics or smells that make it problematic.

## 4. Refactoring

- Explain the change applied to reduce the selected feature.
- Keep the focus on the actionable feature used in the what-if analysis.

## 5. After The Change

- Report the feature values after the change.
- Explain what improved and what stayed stable.

## 6. What-If Analysis

- Summarize the walk-forward classifier comparison from `results.csv`.
- Explain which classifier was selected and why, with emphasis on Kappa and AUC.
- Use `what_if_summary.csv` to compare A, B+, B and C.
- Discuss the `B+->B` impact row as the estimate of potentially avoidable buggy methods.
