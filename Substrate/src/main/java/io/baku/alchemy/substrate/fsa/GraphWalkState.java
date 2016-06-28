package io.baku.alchemy.substrate.fsa;

import java.util.Collection;
import java.util.Comparator;

import com.google.common.collect.Collections2;

import edu.umd.cs.findbugs.annotations.Nullable;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class GraphWalkState {
    public static final Comparator<GraphWalkState> DEPTH_ORDER =
            Comparator.comparing(GraphWalkState::getDepth);
    
    Node node;
    int depth;
    
    public GraphWalkState(final Node root) {
        this(root, 0);
    }
    
    private GraphWalkState advance(final Node target) {
        return new GraphWalkState(target, depth + 1);
    }
    
    public Collection<GraphWalkTransition> getTransitions() {
        return Collections2.transform(node.getEdges(), e -> new GraphWalkTransition(
                e.getPredicate(), advance(e.getTarget())));
    }
    
    public boolean isTerminal() {
        return node.getEdges().isEmpty();
    }
    
    public static @Nullable GraphWalkState wrap(final @Nullable Fsa fsa) {
    	return fsa == null? null : new GraphWalkState(fsa.getStart());
    }
}
