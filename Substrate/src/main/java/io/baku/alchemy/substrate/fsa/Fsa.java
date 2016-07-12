package io.baku.alchemy.substrate.fsa;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collector;

import io.baku.alchemy.substrate.Scanner;
import io.baku.alchemy.substrate.predicates.CharPredicate;
import io.baku.alchemy.substrate.predicates.KeywordPredicate;
import io.baku.alchemy.substrate.predicates.ParsePredicate;
import io.baku.alchemy.substrate.predicates.TypePredicate;
import io.baku.alchemy.substrate.predicates.ZeroWidthAssertion;
import io.baku.alchemy.substrate.symbols.CharSymbol;
import io.baku.alchemy.substrate.symbols.Symbol;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

/**
 * Represents an NFA-λ with a single accepting state. While it is possible to mutate the internal
 * structure to have multiple accepting states, convenience operations would then not behave as
 * expected.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Fsa implements Cloneable {
    public static final Collector<ParsePredicate, Fsa, Fsa> COLLECTOR = Collector.of(Fsa::new, Fsa::append, Fsa::append);
    
    @Getter
    private Node start, end;
    
    public Fsa() {
        end = start = new Node();
    }
    
    public Fsa append(final ParsePredicate predicate) {
        final Node newEnd = new Node();
        end.addEdge(predicate, newEnd);
        end = newEnd;
        return this;
    }
    
    public Fsa appendType(final String type, final float entropy) {
        return append(new TypePredicate(type, entropy));
    }
    
    public Fsa appendKeyword(final String keyword) {
        return append(new KeywordPredicate(keyword));
    }
    
    public Fsa append(final int codePoint) {
        return append(CharPredicate.matching(codePoint));
    }
    
    public Fsa append(final String string) {
        string.codePoints().forEachOrdered(this::append);
        return this;
    }
    
    public Fsa append(Fsa other) {
    	other = other.clone();
        end.addEdge(other.getStart());
        end = other.getEnd();
        return this;
    }
    
    public Fsa append(final Object subgraph) {
        if (subgraph instanceof ParsePredicate) {
            return append((ParsePredicate)subgraph);
        } else if (subgraph instanceof Fsa) {
            return append((Fsa)subgraph);
        } else if (subgraph instanceof Integer) {
            return append((Integer)subgraph);
        } else {
            throw new IllegalArgumentException("Unsupported subgraph type " + subgraph.getClass());
        }
    }
    
    public Fsa or(Fsa alt) {
    	alt = alt.clone();
        final Node newStart = new Node();
        final Node newEnd = new Node();
        
        newStart.addEdge(start);
        newStart.addEdge(alt.getStart());
        end.getEdges().add(Edge.lambda(newEnd));
        alt.getEnd().addEdge(newEnd);
        
        start = newStart;
        end = newEnd;
        
        return this;
    }
    
    public Fsa or(final String alt) {
        return or(new Fsa().append(alt));
    }
    
    public Fsa or(final ParsePredicate alt) {
        start.addEdge(alt, end);
        return this;
    }
    
    public Fsa or(final int codePoint) {
        return or(CharPredicate.matching(codePoint));
    }
    
    public Fsa orType(final String type, final float entropy) {
        return or(new TypePredicate(type, entropy));
    }
    
    public Fsa orKeyword(final String keyword) {
        return or(new KeywordPredicate(keyword));
    }
    
    public Fsa optional() {
        return or(new Fsa());
    }
    
    public Fsa repeat() {
        return repeat(null);
    }
    
    public Fsa kleeneStar() {
        return repeat().optional();
    }
    
    public Fsa repeat(final ParsePredicate delimiter) {
    	return repeat(delimiter, false);
    }
    
    public Fsa repeat(final ParsePredicate delimiter, final boolean greedy) {
    	final ParsePredicate terminus;
    	
    	if (greedy) {
    		terminus = new ZeroWidthAssertion(false, true, clone());
    	} else {
    		terminus = null;
    	}
    	
        final Node newEnd = new Node();
        end.addEdge(delimiter, start);
        end.addEdge(terminus, newEnd);
        end = newEnd;
        return this;
    }
    
    public Fsa repeat(final int codePoint) {
        return repeat(CharPredicate.matching(codePoint));
    }
    
    public Fsa kleeneStar(final ParsePredicate delimiter) {
        return repeat(delimiter).optional();
    }
    
    public Fsa kleeneStar(final int codePoint) {
        return kleeneStar(CharPredicate.matching(codePoint));
    }
    
    public Fsa fencedList(final int openingCodePoint, final String itemType,
            final int delimiterCodePoint, final int closingCodePoint) {
        return append(openingCodePoint)
                .append(new Fsa()
                        .appendType(itemType, 1)
                        .kleeneStar(delimiterCodePoint))
                .append(closingCodePoint);
    }
    
    public Fsa eof() {
        return append(ParsePredicate.EOF);
    }

    @Value
    private static class StaticState {
        int inputCursor;
        Node node;
    }
    
    @RequiredArgsConstructor
    private static class BfsState<T extends Symbol> {
        final Scanner<T> inputState;
        final GraphWalkState g;
        
        final StaticState getStaticState() {
            return new StaticState(inputState.getCursor(), g.getNode());
        }
    }
    
    public <T extends Symbol> Scanner<T> consume(final Scanner<T> input) {
        final PriorityQueue<BfsState<T>> queue = new PriorityQueue<>(
                Comparator.comparing(p -> p.g, GraphWalkState.DEPTH_ORDER));
        final Set<StaticState> seen = new HashSet<>();
        
        queue.add(new BfsState<>(input, new GraphWalkState(start)));
        
        while (!queue.isEmpty()) {
            final BfsState<T> s = queue.remove();
            
            if (seen.add(s.getStaticState())) {
                if (s.g.isTerminal()) {
                    return s.inputState;
                }
                
                for (final GraphWalkTransition t : s.g.getTransitions()) {
                    if (t.getPredicate() == null) {
                        queue.add(new BfsState<>(s.inputState, t.getTarget()));
                    } else if (t.getPredicate().test(s.inputState)) {
                        queue.add(new BfsState<>(s.inputState.advance(
                                t.getPredicate().getWidth(), false), t.getTarget()));
                    }
                }
            }
        }
        
        return null;
    }
    
    public boolean accepts(final Scanner<? extends Symbol> input) {
        return consume(input) != null;
    }
    
    public boolean accepts(final List<? extends Symbol> input) {
        return accepts(new Scanner.Simple<>(input));
    }
    
    public boolean accepts(final String input) {
        return accepts(CharSymbol.fromString(input));
    }
    
    private static Node cloneSubgraph(final Map<Node, Node> nodes, final Node node) {
        Node clone = nodes.get(node);
        // We can't just computeIfAbsent because we'll want to recurse with the updated map.
        if (clone == null) {
            clone = new Node();
            nodes.put(node, clone);
            
            for(final Edge edge : node.getEdges()) {
                clone.addEdge(edge.getPredicate(), cloneSubgraph(nodes, edge.getTarget()));
            }
        }
        
        return clone;
    }
    
    @Override
    public Fsa clone() {
        final Map<Node, Node> nodes = new HashMap<>();
        return new Fsa(cloneSubgraph(nodes, start), nodes.get(end));
    }
}
