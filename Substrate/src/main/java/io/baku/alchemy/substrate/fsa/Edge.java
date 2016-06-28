package io.baku.alchemy.substrate.fsa;

import edu.umd.cs.findbugs.annotations.Nullable;
import io.baku.alchemy.substrate.predicates.ParsePredicate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Edge {
    /**
     * Null predicates indicate lambda transitions.
     */
    @Nullable private final ParsePredicate predicate;
    private final Node target;
    
    public static Edge lambda(final Node target) {
        return new Edge(null, target);
    }
}
