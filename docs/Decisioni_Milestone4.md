# Decisioni — Milestone 4 (Refactoring automatico con LLM verso "zero smell")

> Registro passo-passo di **ogni** decisione, comprese quelle **obbligate** dalla specifica.
> Legenda stato: 🔒 obbligata (specifica/slide) · ✅ scelta (mia, con supporto NotebookLM) · 💡 consigliata (buona prassi) · ❓ da confermare.
> Continuità: M1 (dataset) → M2 (classificazione) → M3 (what-if su NSmells) → **M4 (refactoring LLM)**.

## Metodo di lavoro
- Le scelte già prese si applicano passo-passo; nei dubbi si chiede a me o a NotebookLM.
- Si documenta ogni decisione con: **decisione**, **stato**, **motivazione**, **come è implementata/verificata**.

---

## 1. Scopo della Milestone (NotebookLM)

Investigare se sia possibile ottenere **"zero smell"** tramite **refactoring automatico** con un **LLM** (es. GitHub Copilot), partendo dalle classi *smelly + difettose* identificate in M3. Si sottopongono le classi all'LLM con **diversi livelli di input** e si verifica se il codice generato **compila**, **rimuove gli smell** e **mantiene la funzionalità** originale.

## 2. Attività e livelli di input — 🔒

Per ciascuna classe scelta, si eseguono tre condizioni sperimentali con crescente contesto di test fornito all'LLM:
1. **Nessun test** — refactoring "al buio", solo codice sorgente.
2. **Test black-box** — si forniscono test funzionali di base.
3. **Test ad alta coverage** — si forniscono test che coprono ampiamente la classe.

Per ogni combinazione (classe × livello) si misurano tre esiti: **compila (sì/no)**, **smell residui** (ri-scansione), **funzionalità preservata** (i test passano).

**Origine dei test (imposto, NotebookLM):** i test usati per caratterizzare le classi e guidare l'LLM devono essere **sviluppati da me**, non quelli nativi di AVRO.
- **Disabilitare/rimuovere** tutti i test nativi del progetto Apache AVRO dalla copia di lavoro.
- Utilizzare e **documentare** i test progettati manualmente (**Black-box / Category Partition**) e quelli generati nelle fasi precedenti del corso (**Random, LLM, Coverage-guided**).
- La **coverage** (per distinguere livello 2 "black-box" da livello 3 "alta coverage") si misura con **JaCoCo**.

## 3. Deliverable — 🔒

- **Codice rifattorizzato** prodotto dall'LLM nelle varie condizioni.
- **`classes.txt`** — elenco delle **2 classi** su cui si è lavorato per la parte di testing, in **ordine alfabetico** e con il **path completo del package**.

## 4. Selezione delle 2 classi — ✅ (algoritmo del nome, NotebookLM)

**Procedura imposta (3 step):**
1. **Ranking:** ordinare le classi dell'ultima release (**1.5.4**) per **NSmells** decrescente. *(Considerate le sole classi **top-level** — una per file — perché NSmells è un conteggio **per-file** replicato sulle classi annidate; tie-break su WMC decrescente.)*
2. **Filtraggio (scelta soggettiva documentata):** scartate le classi **banali** con due criteri combinati:
   - **complessità** — WMC < 10 (pochi metodi / metodi troppo semplici);
   - **sostanza** — NSmells < 3 (troppo pochi smell: il "delta" qualitativo post-refactoring sarebbe povero e poco indicativo delle capacità dell'LLM — indicazione NotebookLM).
   Restano **N = 37** classi.
3. **Algoritmo del nome:** prima lettera del **nome** = **M** (Matteo) = 13ª lettera → **X = 13 mod 5 = 3** → si selezionano la **4ª** (prima+3) e la **quartultima** (ultima−3 = posizione 34) del ranking filtrato.

**Classi selezionate:**

| Ruolo | Posizione | Classe | NSmells | WMC | LOC | Buggy |
|---|---|---|---|---|---|---|
| Testa (complesso) | 4ª | **org.apache.avro.generic.GenericData** | 15 | 136 | 633 | yes |
| Coda (più semplice) | 34ª (N−3) | **org.apache.avro.io.JsonDecoder** | 3 | 78 | 424 | yes |

**Motivazione della coppia (NotebookLM):** l'algoritmo "testa e coda" fa lavorare su **due livelli di difficoltà** (molti smell/molto complessa vs meno smell/più semplice), per discutere come i livelli di input dell'LLM incidano al variare della complessità. La testa (GenericData) è **stabile** rispetto al filtro (4ª per NSmells con qualsiasi soglia ragionevole). Per la coda si è aggiunto il vincolo **NSmells ≥ 3** su indicazione di NotebookLM: una classe con **1 solo smell** (era `ByteBufferOutputStream`) darebbe un'analisi del delta troppo povera; `JsonDecoder` — 3 smell ma **WMC 78** di logica di parsing — offre "sostanza algoritmica" su cui l'LLM può effettivamente ragionare, pur restando il task più semplice della coppia. Lavorare a livello di **classe** (non di singolo metodo) è volutamente più difficile per l'LLM.

**Alternativa considerata:** NSmells ≥ 2 → coda = `org.apache.avro.util.WeakIdentityHashMap` (2 smell, WMC 17); scartata perché meno ricca di logica di JsonDecoder.

**Deliverable `classes.txt`** (root del progetto), nomi completi in ordine alfabetico:
```
org.apache.avro.generic.GenericData
org.apache.avro.io.JsonDecoder
```

**Testabilità (Mockito):** dipendenze complesse non escludono una classe — vanno gestite con **mock** per isolare la SUT (indicazione del corso).

## 5. Protocollo di verifica — 💡

- **Compilazione:** build Maven del modulo AVRO dopo la sostituzione della classe.
- **Smell residui:** ri-scansione **SonarCloud source-only** (stessa modalità di M1) sul file rifattorizzato → confronto NSmells prima/dopo.
- **Funzionalità preservata:** esecuzione dei **test** (esistenti nel repo AVRO e/o scritti da me per i livelli 2–3); una regressione = funzionalità non preservata.
- **Coverage dei test forniti:** misurata con **JaCoCo** per definire i livelli "black-box" vs "alta coverage".

## 6. Threats to Validity — specifici di M4

- **Non determinismo dell'LLM:** stesse condizioni possono dare output diversi; serve fissare/annotare modello, versione e prompt per la riproducibilità.
- **Sensibilità al prompt:** il risultato dipende dalla formulazione; va documentato il prompt esatto.
- **Definizione di "smell":** la ri-scansione deve usare gli **stessi ruleset** di M1, altrimenti il confronto non è valido.
- **Funzionalità vs test:** "funzionalità preservata" è verificata solo fin dove arrivano i test; una coverage bassa può mascherare regressioni.
- **Rischio di over-fitting al test:** fornendo test ad alta coverage, l'LLM potrebbe adattare il codice ai test più che preservare il comportamento reale.

## 7. Piano operativo (task)
- **M4-0** Roadmap + shortlist classi *(questo documento)*. ✔️
- **M4-1** Algoritmo del nome, scelta delle 2 classi, `classes.txt`. ✔️
- **M4-2** Estrarre il sorgente delle 2 classi (release 1.5.4), disabilitare i test nativi, scrivere i test propri (Black-box/Category Partition + generati) e definire i 3 livelli di input (coverage via JaCoCo).
- **M4-3** Eseguire il refactoring LLM (Copilot) per le 3 condizioni × 2 classi *(esecuzione locale dell'utente)*, annotando **modello, versione e prompt esatti**.
- **M4-4** Verifiche per ogni combinazione: **compila** / **smell residui** (ri-scansione SonarCloud stesso ruleset di M1) / **test passano**; tabella risultati (classe × livello × esito).
- **M4-5** Confluenza nel **report finale integrato** M1→M4.

## 8. Punti aperti / risolti
1. ~~Quali **2 classi**~~ **RISOLTO** (§4): GenericData (testa) + JsonDecoder (coda), via algoritmo del nome (M → X=3) + filtro WMC ≥ 10 **e NSmells ≥ 3**.
2. **Soglie di coverage** per distinguere livello "black-box" da "alta coverage": da fissare (misura JaCoCo) in M4-2. ❓
3. **LLM** da usare: GitHub Copilot (default del corso) — confermare la versione/modello effettivamente disponibile. ❓
4. **Convenzione lettera** = nome (Matteo → M → X=3), confermata dall'utente. Se il docente intende il cognome (La Gioia → L → X=2), testa=BinaryData: da verificare a lezione. ❓
