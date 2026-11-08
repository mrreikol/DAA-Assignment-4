package textanalysis;

import java.util.ArrayList;
import java.util.List;

public final class StringMatching {
    private static final long BASE = 65536;
    private static final long MODULUS = 1_000_000_007L;
    private StringMatching() { }

    private static MatchResult emptyPattern(int n) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i <= n; i++) indices.add(i);
        return new MatchResult(indices, 0, 0);
    }

    public static MatchResult naive(String text, String pattern) {
        int n = text.length(), m = pattern.length();
        if (m == 0) return emptyPattern(n);
        List<Integer> indices = new ArrayList<>();
        long comparisons = 0;
        for (int i = 0; i <= n - m; i++) {
            int j = 0;
            while (j < m) {
                comparisons++;
                if (text.charAt(i + j) != pattern.charAt(j)) break;
                j++;
            }
            if (j == m) indices.add(i);
        }
        return new MatchResult(indices, 0, comparisons);
    }

    public static MatchResult kmp(String text, String pattern) {
        int n = text.length(), m = pattern.length();
        if (m == 0) return emptyPattern(n);
        if (m > n) return new MatchResult(List.of(), 0, 0);
        int[] lps = new int[m];
        long preprocessing = 0, search = 0;
        for (int i = 1, matched = 0; i < m;) {
            preprocessing++;
            if (pattern.charAt(i) == pattern.charAt(matched)) {
                lps[i++] = ++matched;
            } else if (matched > 0) {
                matched = lps[matched - 1];
            } else {
                lps[i++] = 0;
            }
        }
        List<Integer> indices = new ArrayList<>();
        for (int i = 0, j = 0; i < n;) {
            search++;
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
                if (j == m) {
                    indices.add(i - m);
                    j = lps[j - 1];
                }
            } else if (j > 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }
        return new MatchResult(indices, preprocessing, search);
    }

    public static MatchResult rabinKarp(String text, String pattern) {
        return rabinKarp(text, pattern, MODULUS);
    }

    static MatchResult rabinKarp(String text, String pattern, long modulus) {
        int n = text.length(), m = pattern.length();
        if (m == 0) return emptyPattern(n);
        if (m > n) return new MatchResult(List.of(), 0, 0);
        long patternHash = 0, windowHash = 0, power = 1, comparisons = 0;
        for (int i = 0; i < m; i++) {
            patternHash = (patternHash * BASE + pattern.charAt(i)) % modulus;
            windowHash = (windowHash * BASE + text.charAt(i)) % modulus;
            if (i < m - 1) power = power * BASE % modulus;
        }
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i <= n - m; i++) {
            if (patternHash == windowHash) {
                int j = 0;
                while (j < m) {
                    comparisons++;
                    if (text.charAt(i + j) != pattern.charAt(j)) break;
                    j++;
                }
                if (j == m) indices.add(i);
            }
            if (i < n - m) {
                windowHash = (windowHash - text.charAt(i) * power % modulus + modulus) % modulus;
                windowHash = (windowHash * BASE + text.charAt(i + m)) % modulus;
            }
        }
        return new MatchResult(indices, 0, comparisons);
    }
}