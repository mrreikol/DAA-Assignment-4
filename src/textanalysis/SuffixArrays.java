package textanalysis;

import java.util.Arrays;

public final class SuffixArrays {
    private SuffixArrays() { }
    public static int[] build(String text) {
        Integer[] order = new Integer[text.length()];
        for (int i = 0; i < order.length; i++) order[i] = i;
        Arrays.sort(order, (left, right) -> {
            int i = left, j = right;
            while (i < text.length() && j < text.length()) {
                int comparison = Character.compare(text.charAt(i), text.charAt(j));
                if (comparison != 0) return comparison;
                i++;
                j++;
            }
            return Integer.compare(text.length() - left, text.length() - right);
        });
        int[] result = new int[order.length];
        for (int i = 0; i < result.length; i++) result[i] = order[i];
        return result;
    }
}