import pandas as pd, matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import os

os.makedirs("output/report/figs", exist_ok=True)
order = ["Random Forest","K-Nearest Neighbors","Naive Bayes"]
short = {"Random Forest":"RF","K-Nearest Neighbors":"IBk","Naive Bayes":"NB"}
colors = {"Random Forest":"#2c7fb8","K-Nearest Neighbors":"#41ab5d","Naive Bayes":"#d95f0e"}

# ---- Fig 1: boxplot Kappa across 16 datasets (CV baseline) ----
sens = pd.read_csv("output/sensitivity/results.csv")
data = [sens[sens.Classifier==c]["Kappa"].values for c in order]
fig, ax = plt.subplots(figsize=(7,4.2))
bp = ax.boxplot(data, patch_artist=True, widths=0.55,
                medianprops=dict(color="black"))
for patch,c in zip(bp["boxes"],order):
    patch.set_facecolor(colors[c]); patch.set_alpha(0.75)
ax.set_xticklabels([short[c] for c in order])
ax.set_ylabel("Cohen's Kappa")
ax.set_title("Stabilità del Kappa tra i 16 dataset (10x10 CV, baseline)")
ax.yaxis.grid(True, ls=":", alpha=0.6); ax.set_axisbelow(True)
fig.tight_layout(); fig.savefig("output/report/figs/boxplot_kappa.png", dpi=150); plt.close(fig)

# ---- Fig 2: CV vs walk-forward, Kappa & NPofB20 (principal, baseline none/none) ----
m = pd.read_csv("output/report/m2_matrix.csv")
cv = m[m.Config=="cv_none_none"].set_index("Classifier")
wf = m[m.Config=="wf_none_none"].set_index("Classifier")
import numpy as np
x = np.arange(len(order)); w=0.2
fig, ax = plt.subplots(figsize=(7.5,4.4))
ax.bar(x-1.5*w,[cv.loc[c,"Kappa"] for c in order],w,label="Kappa CV",color="#2c7fb8")
ax.bar(x-0.5*w,[wf.loc[c,"Kappa"] for c in order],w,label="Kappa WF",color="#a6bddb")
ax.bar(x+0.5*w,[cv.loc[c,"NPofB20"] for c in order],w,label="NPofB20 CV",color="#d95f0e")
ax.bar(x+1.5*w,[wf.loc[c,"NPofB20"] for c in order],w,label="NPofB20 WF",color="#fdae6b")
ax.set_xticks(x); ax.set_xticklabels([short[c] for c in order])
ax.set_ylabel("valore"); ax.set_ylim(0,1.05)
ax.set_title("Cross-validation vs Walk-forward (variante principale, no FS/balancing)")
ax.legend(ncol=2, fontsize=8); ax.yaxis.grid(True, ls=":", alpha=0.6); ax.set_axisbelow(True)
fig.tight_layout(); fig.savefig("output/report/figs/bars_cv_vs_wf.png", dpi=150); plt.close(fig)
print("charts written:", os.listdir("output/report/figs"))
