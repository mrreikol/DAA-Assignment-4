#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")/.."
mkdir -p out
java -m jdk.compiler/com.sun.tools.javac.Main -d out src/textanalysis/*.java
java -cp out textanalysis.AlgorithmTests
java -cp out textanalysis.Main
java -cp out textanalysis.Benchmark
python3 scripts/plot_results.py