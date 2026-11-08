package textanalysis;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;

public final class Benchmark {
    private static final long SEED = 20261101L;
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final int RUNS = 5;
    private static volatile Object sink;
    static String randomString(Random random, int length) {
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) result.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        return result.toString();
    }
    private static void measure(PrintWriter raw, PrintWriter summary, String algorithm,
                                String scenario, int n, int m, long prep, long search,
                                Supplier<Object> action) {
        for (int i = 0; i < 20; i++) sink = action.get();
        long total = 0;
        for (int run = 1; run <= RUNS; run++) {
            long start = System.nanoTime();
            Object result = action.get();
            long elapsed = System.nanoTime() - start;
            sink = result;
            total += elapsed;
            raw.printf(Locale.ROOT, "%s,%s,%d,%d,%d,%d%n", algorithm, scenario, n, m, run, elapsed);
        }
        summary.printf(Locale.ROOT, "%s,%s,%d,%d,%.6f,%d,%d%n",
            algorithm, scenario, n, m, total / (RUNS * 1_000_000.0), prep, search);
    }
    public static void main(String[] args) throws Exception {
        Path directory = Path.of("results");
        Files.createDirectories(directory);
        Files.writeString(directory.resolve("environment.txt"),
            "Java: " + System.getProperty("java.runtime.version") + "\nVM: " + System.getProperty("java.vm.name")
            + "\nOS: " + System.getProperty("os.name") + " " + System.getProperty("os.arch")
            + "\nAvailable processors: " + Runtime.getRuntime().availableProcessors()
            + "\nSeed: " + SEED + "\nAlphabet: " + ALPHABET + "\nWarmup calls per workload: 20\nTimed runs: 5\n");
        Random random = new Random(SEED);
        try (PrintWriter raw = new PrintWriter(directory.resolve("raw.csv").toFile());
             PrintWriter summary = new PrintWriter(directory.resolve("summary.csv").toFile())) {
            raw.println("algorithm,scenario,n,m,run,nanoseconds");
            summary.println("algorithm,scenario,n,m,average_ms,preprocessing_comparisons,search_comparisons");
            for (int n : new int[]{100, 500, 1000}) {
                String a = randomString(random, n), b = randomString(random, n);
                measure(raw, summary, "LCS", "random", n, n, 0, 0, () -> Lcs.length(a, b));
                measure(raw, summary, "Edit Distance", "random", n, n, 0, 0, () -> EditDistance.distance(a, b));
            }
            for (int n : new int[]{10000, 50000, 100000}) {
                String text = randomString(random, n);
                String present = text.substring(n / 2, n / 2 + 20);
                String absent;
                do { absent = randomString(random, 20); }
                while (!StringMatching.naive(text, absent).indices().isEmpty());
                for (String scenario : new String[]{"present", "absent"}) {
                    String pattern = scenario.equals("present") ? present : absent;
                    List<Integer> expected = StringMatching.naive(text, pattern).indices();
                    for (String algorithm : new String[]{"Naive", "KMP", "Rabin-Karp"}) {
                        Supplier<Object> action = switch (algorithm) {
                            case "Naive" -> () -> StringMatching.naive(text, pattern);
                            case "KMP" -> () -> StringMatching.kmp(text, pattern);
                            default -> () -> StringMatching.rabinKarp(text, pattern);
                        };
                        MatchResult result = (MatchResult) action.get();
                        if (!expected.equals(result.indices())) throw new AssertionError("Matching results disagree");
                        measure(raw, summary, algorithm, scenario, n, pattern.length(),
                            result.preprocessingComparisons(), result.searchComparisons(), action);
                    }
                }
            }
            for (int n : new int[]{1000, 5000, 10000}) {
                String text = randomString(random, n);
                int[] sa = SuffixArrays.build(text);
                measure(raw, summary, "Kasai", "random", n, 0, 0, 0, () -> Kasai.lcp(text, sa));
            }
        }
        System.out.println("Saved results/raw.csv, results/summary.csv and results/environment.txt");
    }
}