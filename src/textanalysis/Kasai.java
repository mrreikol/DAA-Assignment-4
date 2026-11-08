package textanalysis;

public final class Kasai {
    private Kasai() { }
    public static int[] lcp(String text, int[] suffixArray) {
        int n = text.length();
        if (suffixArray.length != n) throw new IllegalArgumentException("Suffix array length mismatch");
        int[] rank = new int[n];
        boolean[] seen = new boolean[n];
        for (int i = 0; i < n; i++) {
            int suffix = suffixArray[i];
            if (suffix < 0 || suffix >= n || seen[suffix]) {
                throw new IllegalArgumentException("Suffix array must be a permutation");
            }
            seen[suffix] = true;
            rank[suffix] = i;
        }
        int[] lcp = new int[n];
        for (int i = 0, height = 0; i < n; i++) {
            int r = rank[i];
            if (r == 0) {
                height = 0;
                continue;
            }
            int j = suffixArray[r - 1];
            while (i + height < n && j + height < n
                    && text.charAt(i + height) == text.charAt(j + height)) height++;
            lcp[r] = height;
            if (height > 0) height--;
        }
        return lcp;
    }
}