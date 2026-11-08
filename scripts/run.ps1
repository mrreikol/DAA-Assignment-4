$ErrorActionPreference = "Stop"
Set-Location (Join-Path $PSScriptRoot "..")
New-Item -ItemType Directory -Force out | Out-Null
$sources = Get-ChildItem src/textanalysis/*.java | ForEach-Object { $_.FullName }
javac -d out $sources
if ($LASTEXITCODE -ne 0) { throw "Compilation failed" }
foreach ($entry in @("AlgorithmTests", "Main", "Benchmark")) {
    java -cp out "textanalysis.$entry"
    if ($LASTEXITCODE -ne 0) { throw "$entry failed" }
}
python scripts/plot_results.py
if ($LASTEXITCODE -ne 0) { throw "Plot generation failed" }