package io.baku.alchemy.substrate.fsa;

import edu.umd.cs.findbugs.annotations.Nullable;
import io.baku.alchemy.substrate.predicates.ParsePredicate;
import lombok.Value;

@Value
public class GraphWalkTransition {
    /**
     * Null predicates indicate lambda transitions.
     */
    @Nullable ParsePredicate predicate;
    GraphWalkState target;
}
