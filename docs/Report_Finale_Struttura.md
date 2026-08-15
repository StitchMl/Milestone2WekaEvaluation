# Report finale ISW2 — Linee guida (NotebookLM)

> "The Full Story": dall'analisi storica di un progetto buggato al tentativo di migliorarne la manutenibilità con l'AI.

## 1. Struttura e filo narrativo
Separare nettamente **Metodologia** e **Risultati**. Ordine:
1. **Introduzione** — progetto Apache AVRO + obiettivi globali dello studio.
2. **Metodologia (M1–M4)** — come ho costruito il dataset (M1), selezionato i modelli (M2), impostato gli scenari what-if (M3), configurato il refactoring LLM (M4).
3. **Risultati (M1–M4)** — dati ottenuti (accuratezza modelli, bug prevenibili, successo del refactoring).
4. **Threats to Validity** — limiti e assunzioni.
5. **Conclusioni e Take-away** — cosa ho imparato.

Filo narrativo: *"Abbiamo costruito un dataset per identificare i bug (M1–M2), scoperto che molti sono legati agli smell (M3), testato se l'AI può rimuoverli automaticamente per prevenire bug futuri (M4)."*

## 2. Bilanciamento
- **Testo:** ~**12 pagine A4**, **Arial 10, interlinea singola**.
- **Appendice (obbligatoria):** figure, tabelle, listati di codice — **NON contano** nel limite delle 12 pagine.
- **Comprimere:** metodologia M3 e M4 (passi strutturati e imposti).
- **Espandere:** metodologia M1 e M2 (molte scelte soggettive da giustificare: quali feature, release senza data, quale balancing).

## 3. Risultati M4
- **Matrice di validazione:** tabella 5×5 varianti (C0–C4) × test suite (BB, CF, MT…) con **Pass/Fail**.
- **Delta qualitativo:** variazione di smell density, complessità (WMC), mutation score.
- **Findings:**
  - **Smell API:** l'LLM non può rimuovere smell vincolati alla firma dei metodi (es. troppi parametri) senza rompere la compatibilità.
  - **Context Impact:** discutere se aggiungere test nel prompt (C2–C4) abbia ridotto le allucinazioni / migliorato il codice rispetto a C1.
  - **Equivalenza funzionale:** usare il caso del **bug fix silente** (`writeEscapedString`) per mostrare che test "verdi" non garantiscono comportamento invariato (problema dell'oracolo).

## 4. Threats to Validity — sezione **unica consolidata** a fine report
- **M1/M2:** bias dello **snoring** (falsi negativi nelle release recenti); potenziale **data leakage** se usato Total Proportion.
- **M3:** assunzione di **invarianza** (azzerare gli smell non altera altre feature come LOC/Churn).
- **M4:** **non determinismo** dell'LLM; rischio **data leakage** (il modello potrebbe aver già visto AVRO in training).

## 5. Formato e requisiti tecnici
- **Formato:** esclusivamente **PDF con link navigabili** tra testo e appendice.
- **Citazioni:** versioni esplicite dei tool (Weka, SonarCloud, JaCoCo, PIT, Copilot) e **prompt usati** (riproducibilità).
- **Abstract:** non richiesto formalmente; l'introduzione funge da sintesi di contesto e problema.
- **Deliverable aggiuntivo:** allegare via email anche `classes.txt` (classi analizzate, ordine alfabetico).

## 6. Valutazione ed errori da evitare
Il docente valuta l'**Intellectual Control**:
- **Non conta l'accuratezza:** Kappa basso o refactoring fallito non abbassano il voto se metodologia rigorosa e discussione onesta.
- **No "Tool Log":** non "il tool ha dato 37"; spiegare cosa significa quel 37 per la salute del software (analisi, non lettura dati).
- **Qualità del codice:** il proprio tool dev'essere privo di smell e giustificabile all'orale.
- **Errore comune:** mettere analisi/commenti nelle didascalie delle tabelle in appendice; l'analisi sta **solo** nelle 12 pagine.
