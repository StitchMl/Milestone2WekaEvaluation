#!/usr/bin/env python3
"""
Aggregate the Milestone 2 experiment matrix into one comparative table.

Reads every output/matrix/<tag>/results.csv (one row per classifier, with the
Balancing / FeatureSelection / ValidationStrategy columns embedded) and, if present,
output/sensitivity/results.csv. Produces:
  - output/report/m2_matrix.csv     (tidy long table of every config x classifier)
  - output/report/m2_sensitivity.csv (per-variant metrics from the sensitivity sweep)
and prints the leaderboards by Kappa for CV and walk-forward.
"""
import csv, glob, os

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
MATRIX = os.path.join(ROOT, "output", "matrix")
SENS = os.path.join(ROOT, "output", "sensitivity", "results.csv")
OUTDIR = os.path.join(ROOT, "output", "report")

METRICS = ["Kappa", "AUC", "NPofB20", "F1", "Precision", "Recall", "Accuracy"]
KEEP = ["ValidationStrategy", "FeatureSelection", "Balancing", "Classifier"] + METRICS


def read_results(path):
    with open(path, newline="", encoding="utf-8") as f:
        return list(csv.DictReader(f))


def main():
    rows = []
    for res in sorted(glob.glob(os.path.join(MATRIX, "*", "results.csv"))):
        rows.extend(read_results(res))
    if not rows:
        print("No output/matrix/*/results.csv found — run tools\\run-matrix.ps1 first.")
        return
    os.makedirs(OUTDIR, exist_ok=True)

    # tidy long table
    out = os.path.join(OUTDIR, "m2_matrix.csv")
    with open(out, "w", newline="", encoding="utf-8") as f:
        w = csv.writer(f)
        w.writerow(KEEP)
        for r in rows:
            w.writerow([r.get(k, "") for k in KEEP])
    print(f"wrote {out}  ({len(rows)} config x classifier rows)")

    def fmt(x):
        try: return f"{float(x):.3f}"
        except Exception: return x

    for val in ["cross-validation", "walk-forward"]:
        sub = [r for r in rows if r["ValidationStrategy"] == val]
        if not sub:
            continue
        sub.sort(key=lambda r: float(r["Kappa"]), reverse=True)
        print(f"\n=== {val} — leaderboard per Kappa (top 8) ===")
        print(f"{'FS':6} {'Balancing':13} {'Classifier':22} {'Kappa':>6} {'AUC':>6} {'NPofB20':>8} {'F1':>6}")
        for r in sub[:8]:
            print(f"{r['FeatureSelection']:6} {r['Balancing']:13} {r['Classifier']:22} "
                  f"{fmt(r['Kappa']):>6} {fmt(r['AUC']):>6} {fmt(r['NPofB20']):>8} {fmt(r['F1']):>6}")

    # sensitivity
    if os.path.isfile(SENS):
        srows = read_results(SENS)
        so = os.path.join(OUTDIR, "m2_sensitivity.csv")
        with open(so, "w", newline="", encoding="utf-8") as f:
            w = csv.writer(f)
            w.writerow(["Dataset", "Classifier"] + METRICS)
            for r in srows:
                w.writerow([r["Dataset"], r["Classifier"]] + [r.get(m, "") for m in METRICS])
        print(f"\nwrote {so}  ({len(srows)} rows over the sensitivity variants)")


if __name__ == "__main__":
    main()
