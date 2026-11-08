package textanalysis;

import java.util.Arrays;

public final class Main {
    public static void main(String[] args) {
        System.out.println("LCS length: " + Lcs.length("ABCBDAB", "BDCABA"));
        System.out.println("Edit distance: " + EditDistance.distance("kitten", "sitting"));
        System.out.println("Naive matches: " + StringMatching.naive("bananana", "ana").indices());
        System.out.println("KMP matches: " + StringMatching.kmp("bananana", "ana").indices());
        System.out.println("Rabin-Karp matches: " + StringMatching.rabinKarp("bananana", "ana").indices());
        int[] sa = {5, 3, 1, 0, 4, 2};
        System.out.println("banana suffix array: " + Arrays.toString(sa));
        System.out.println("banana LCP: " + Arrays.toString(Kasai.lcp("banana", sa)));
    }
}