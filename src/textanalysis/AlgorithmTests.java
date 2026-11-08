package textanalysis;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public final class AlgorithmTests {
    private static int assertions;
    private static void equal(Object expected, Object actual) {
        assertions++;
        if (!expected.equals(actual)) throw new AssertionError("Expected " + expected + ", got " + actual);
    }
    private static void matches(String text, String pattern, List<Integer> expected) {
        equal(expected, StringMatching.naive(text, pattern).indices());
        equal(expected, StringMatching.kmp(text, pattern).indices());
        equal(expected, StringMatching.rabinKarp(text, pattern).indices());
    }
    private static void array(int[] expected, int[] actual) {
        equal(Arrays.toString(expected), Arrays.toString(actual));
    }
    private static void invalid(Runnable action) {
        assertions++;
        try { action.run(); } catch (IllegalArgumentException expected) { return; }
        throw new AssertionError("Expected IllegalArgumentException");
    }
    public static void main(String[] args) {
        equal(3, Lcs.length("abc", "abc"));
        equal(0, Lcs.length("abc", "XYZ"));
        equal(0, Lcs.length("", "abc"));
        equal(0, Lcs.length("abc", ""));
        equal(4, Lcs.length("ABCBDAB", "BDCABA"));
        equal(0, EditDistance.distance("abc", "abc"));
        equal(3, EditDistance.distance("", "abc"));
        equal(3, EditDistance.distance("abc", ""));
        equal(1, EditDistance.distance("cat", "cut"));
        equal(1, EditDistance.distance("cat", "cart"));
        equal(1, EditDistance.distance("cart", "cat"));
        equal(3, EditDistance.distance("kitten", "sitting"));
        matches("abcxxx", "abc", List.of(0));
        matches("xxabcxx", "abc", List.of(2));
        matches("xxxabc", "abc", List.of(3));
        matches("aaaaa", "aaa", List.of(0, 1, 2));
        matches("abcabc", "abc", List.of(0, 3));
        matches("abc", "x", List.of());
        matches("abc", "abcd", List.of());
        matches("abc", "", List.of(0, 1, 2, 3));
        matches("", "", List.of(0));
        matches("", "a", List.of());
        matches("a\u03a9a\u03a9", "\u03a9", List.of(1, 3));
        equal(List.of(1), StringMatching.rabinKarp("ca", "a", 2).indices());
        array(new int[]{0, 1, 3, 0, 0, 2}, Kasai.lcp("banana", new int[]{5, 3, 1, 0, 4, 2}));
        array(new int[]{}, Kasai.lcp("", new int[]{}));
        array(new int[]{0}, Kasai.lcp("a", new int[]{0}));
        array(new int[]{0, 1, 2, 3}, Kasai.lcp("aaaa", new int[]{3, 2, 1, 0}));
        invalid(() -> Kasai.lcp("ab", new int[]{0}));
        invalid(() -> Kasai.lcp("ab", new int[]{0, 0}));
        invalid(() -> Kasai.lcp("ab", new int[]{0, 2}));
        Random random = new Random(2026);
        for (int trial = 0; trial < 300; trial++) {
            String text = Benchmark.randomString(random, random.nextInt(35));
            String pattern = Benchmark.randomString(random, random.nextInt(9));
            matches(text, pattern, StringMatching.naive(text, pattern).indices());
            int[] sa = SuffixArrays.build(text);
            int[] expected = new int[text.length()];
            for (int r = 1; r < sa.length; r++) {
                int h = 0;
                while (sa[r] + h < text.length() && sa[r - 1] + h < text.length()
                        && text.charAt(sa[r] + h) == text.charAt(sa[r - 1] + h)) h++;
                expected[r] = h;
            }
            array(expected, Kasai.lcp(text, sa));
        }
        System.out.println("PASS: " + assertions + " assertions, including 300 randomized trials.");
    }
}