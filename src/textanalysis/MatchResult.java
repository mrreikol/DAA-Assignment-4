package textanalysis;

import java.util.List;

public record MatchResult(List<Integer> indices, long preprocessingComparisons,
                          long searchComparisons) {
    public MatchResult {
        indices = List.copyOf(indices);
    }
}