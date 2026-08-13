# run-matrix.ps1 — Milestone 2 experiment matrix on the principal variant + sensitivity sweep.
# Sweeps: validation {cross-validation, walk-forward} x feature-selection {none, CFS} x balancing {none, SMOTE, under, over}
# = 16 runs on datasets\principal (each writes output\matrix\<tag>\results.csv), plus 1 sensitivity run over datasets\all.
# Run from the project root:  .\tools\run-matrix.ps1
$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

$common = @(
  "--data-dir=datasets\principal",
  "--class-attribute=Buggy", "--positive-class=yes",
  "--temporal-attribute=ReleaseId", "--size-attribute=LOC", "--whatif=false"
)
$fsMap   = [ordered]@{ none = "none"; cfs = "wrapper" }
$balList = @("none", "smote", "undersampling", "oversampling")
$vals    = [ordered]@{
  cv = @("--validation=cross-validation", "--runs=10", "--folds=10")
  wf = @("--validation=walk-forward", "--min-train-periods=1")
}

$n = 0
foreach ($v in $vals.Keys) {
  foreach ($fs in $fsMap.Keys) {
    foreach ($bal in $balList) {
      $n++
      $tag = "$($v)_$($fs)_$($bal)"
      Write-Host "=== [$n/16] RUN $tag ===" -ForegroundColor Cyan
      $cliArgs = $common + $vals[$v] + @("--feature-selection=$($fsMap[$fs])", "--balancing=$bal", "--output-dir=output\matrix\$tag")
      & .\run-analysis.cmd @cliArgs
    }
  }
}

Write-Host "=== SENSITIVITY (datasets\all, baseline cv/none/none) ===" -ForegroundColor Cyan
& .\run-analysis.cmd --data-dir=datasets\all --class-attribute=Buggy --positive-class=yes `
  --temporal-attribute=ReleaseId --size-attribute=LOC --whatif=false `
  --validation=cross-validation --runs=10 --folds=10 --feature-selection=none --balancing=none `
  --output-dir=output\sensitivity

Write-Host "DONE. Ora l'aggregazione dei output\matrix\*\results.csv" -ForegroundColor Green
