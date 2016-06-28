package io.baku.alchemy.substrate.fsa;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

import io.baku.alchemy.substrate.predicates.ParsePredicate;
import lombok.Getter;

/**
 * A state in an NFA-λ. For convenience, any state without any outgoing transitions is considered
 * an accepting state.
 */
public class Node {
    @Getter
    private final HashSet<Edge> edges = new HashSet<>();
    
    public Node addEdge(final ParsePredicate predicate, final Node target) {
        edges.add(new Edge(predicate, target));
        return this;
    }
    
    public Node addEdge(final Node target) {
        edges.add(Edge.lambda(target));
        return this;
    }
    
    public static final Node graphAccepting(final ParsePredicate predicate) {
        return new Node().addEdge(predicate, new Node());
    }
    
    public String getLabel() {
        return Integer.toHexString(hashCode()); 
    }
    
    @Override
    public String toString() {
        return getLabel() + Arrays.toString(edges.stream()
            .map(e -> Objects.toString(e.getPredicate(), "λ") + " -> " + e.getTarget().getLabel())
            .toArray());
    }
}
