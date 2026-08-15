# Prompt Copilot — Milestone 4 (refactoring LLM verso "zero smell")

Obiettivo: per ciascuna classe (`org.apache.avro.generic.GenericData`, `org.apache.avro.io.JsonDecoder`) generare 4 varianti a contesto di test crescente e verificarle contro l'oracolo C0.

**Regole per tutte le varianti**
- Non cambiare la firma pubblica (API) né il comportamento osservabile.
- Il codice deve compilare su Java 8 e passare **tutti** i miei test (`com.milestone4.*`).
- Ridurre a zero (o al minimo) gli smell SonarCloud **senza** aumentare WMC/LOC.
- Annota per ogni run: modello/versione Copilot e prompt esatto.

Sequenza di verifica per ogni variante:
```
mvn clean test
mvn -Pmutation org.pitest:pitest-maven:mutationCoverage
```
poi ri-scansione SonarCloud (stesso ruleset di M1) e confronto delta (smell, coverage, mutation, WMC, LOC).

---

## C1 — solo codice sorgente
> Refactor the following Java class to remove all code smells reported by SonarSource
> (SonarCloud) rules, following clean code best practices. Do NOT change the public API
> or the observable behavior. Keep it compilable on Java 8. Do not add external
> dependencies. Return only the refactored class.
>
> [incolla il sorgente della classe]

## C2 — codice + test Black-box (BB)
> Same task as C1 (remove all SonarCloud smells, preserve public API and behavior,
> Java 8). Use the following black-box tests as the behavioral specification: the
> refactored class MUST keep them all green.
>
> [sorgente della classe]
> [contenuto di src/test/java/com/milestone4/<classe>/bb/*.java]

## C3 — codice + BB + Control-Flow (cf)
> Same task as C2. In addition to the black-box tests, here are coverage-guided tests
> that exercise the less-covered branches. Preserve behavior so that ALL tests stay green.
>
> [sorgente della classe]
> [bb/*.java]
> [cf/*.java]

## C4 — codice + BB + cf + Mutation (mt)
> Same task as C3. Here is also a mutation-guided suite that pins exact outputs and
> edge cases. The refactoring must keep ALL tests green and must not weaken these
> assertions.
>
> [sorgente della classe]
> [bb/*.java] [cf/*.java] [mt/*.java]

---

### Note operative
- Una variante = un branch (es. `m4-genericdata-c1` … `-c4`), partendo da `m4-genericdata` (C0).
- Se una variante non compila o rompe un test → l'LLM ha introdotto un errore (allucinazione): documentarlo, non "aggiustare a mano" prima di aver registrato l'esito.
- Il file da rifattorizzare è la sola classe target: gli altri 73 file del modulo restano invariati.
