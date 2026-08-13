import matplotlib; matplotlib.use("Agg")
import matplotlib.pyplot as plt, numpy as np, os
os.makedirs("output/report/figs", exist_ok=True)
scen=["A\n(completo)","B+\n(NSmells>0)","B\n(NSmells→0)","C\n(NSmells=0)"]
actual=[450,413,413,37]; pred=[460,420,383,40]
x=np.arange(len(scen)); w=0.38
fig,ax=plt.subplots(figsize=(7.6,4.4))
b1=ax.bar(x-w/2,actual,w,label="Buggy reali",color="#7f7f7f")
b2=ax.bar(x+w/2,pred,w,label="Buggy predette (RF)",color="#2c7fb8")
for bars in (b1,b2):
    for r in bars: ax.annotate(str(int(r.get_height())),(r.get_x()+r.get_width()/2,r.get_height()),
        ha="center",va="bottom",fontsize=8)
# impact arrow B+ -> B on predicted
ax.annotate("", xy=(2+w/2,383), xytext=(1+w/2,420),
    arrowprops=dict(arrowstyle="->",color="#d95f0e",lw=1.6))
ax.text(1.5,440,"−37 predette\n(31 reali evitabili)",color="#d95f0e",fontsize=8,ha="center")
ax.set_xticks(x); ax.set_xticklabels(scen); ax.set_ylabel("n. classi")
ax.set_title("What-if: impatto della rimozione di NSmells sulle classi Buggy")
ax.legend(); ax.yaxis.grid(True,ls=":",alpha=0.6); ax.set_axisbelow(True); ax.set_ylim(0,510)
fig.tight_layout(); fig.savefig("output/report/figs/whatif_scenarios.png",dpi=150); print("ok")
