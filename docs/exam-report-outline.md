# Exam Report Outline

This file is a compact writing scaffold aligned with the exam workflow supported by the codebase.

## 1. Introduction

- State the project goal, and the target system under study.
- Explain that the workflow combines classifier benchmarking and a what-if analysis on a selected feature.

## 2. Why This Method

- Describe how the target method or entity was selected.
- Use `feature_correlations.csv` to discuss both the most correlated feature, and the actionable smell feature selected for the what-if study.
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

- Summarize the 10-times 10-fold cross-validation classifier comparison from `results.csv`.
- Explain which classifier was selected and why, with emphasis on Kappa and AUC.
- Use `what_if_summary.csv` to compare A, B+, B and C.
- Discuss the `B+->B` impact row as the estimate of potentially avoidable buggy methods.

### Results (run 20260506-102634)

Best classifier: **Random Forest** (Kappa = 0.743, AUC = 0.951 on A.arff — wins all 7 metrics).

| Scenario | Instances | Actual buggy | % Buggy | Predicted buggy | Avg P(buggy) |
|---|---:|---:|---:|---:|---:|
| A — full dataset | 3 183 | 462 | 14.5 % | 434 | 0.1440 |
| B+ — NSmells > 0 | 2 355 | 366 | 15.5 % | 342 | 0.1532 |
| B — synthetic (NSmells = 0) | 2 355 | 366 | 15.5 % | 340 | 0.1548 |
| C — real NSmells = 0 | 828 | 96 | 11.6 % | 92 | 0.1179 |

Coherence check: B+ + C = 2 355 + 828 = 3 183 = A ✓ — actual buggy: 366 + 96 = 462 = A ✓

**Impact B+ → B:**

| Metric | Value |
|---|---|
| Paired instances | 2 355 |
| Actual buggy in B+ | 366 |
| Predicted relieved (buggy → clean when NSmells = 0) | 2 |
| Avoidable buggy (actually buggy among the 2 relieved) | 2 |
| **Avoidable buggy share** | **0.55 % (2 / 366)** |
| Average positive-probability reduction | −0.00164 |

### Note on the negative average probability reduction

The average probability reduction is **negative** (−0.00164): setting NSmells to zero causes
Random Forest to assign a *slightly higher* mean buggy probability (0.1548 vs 0.1532 in B+),
even though it classifies 2 fewer instances as buggy.

This is counterintuitive but mathematically consistent. The explanation is:

1. **NSmells has weak predictive weight in the RF model.** Its point-biserial correlation
 with Buggy on A.arff is only 0.101 — ranked 30th out of 54 features. Random Forest
 distributes importance across many stronger features (TotalAuthors, TotalTouches,
 Cyclomatic, etc.), so zeroing NSmells has a marginal effect on most trees.

2. **Feature interaction effects.** In a 50-tree ensemble, NSmells can have locally negative
 partial effects: some trees learned that classes with NSmells = 0 tend to be
 *more* at risk when combined with high values of other smell-correlated features
 (e.g., CodeSmells, SmellDensity remain unchanged in B). Zeroing only NSmells can
 therefore increase the predicted risk for those trees.

3. **Only 2 instances cross the decision boundary.** Precisely 2 instances were predicted
 buggy in B+ but clean in B; both were actually buggy. The global probability averages
 are affected by the many instances that stay on the same side of the boundary but whose
 individual probabilities shift slightly upward.

**Exam talking point.** If asked, the correct answer is: *the impact metric that matters
for the what-if conclusion is `AvoidableBuggyShare` (0.55 %), not the average probability
reduction. The negative reduction does not invalidate the analysis — it confirms that
NSmells is a marginal feature for this RF model, and that ideal refactoring would prevent
only a small fraction of predicted defects.*
