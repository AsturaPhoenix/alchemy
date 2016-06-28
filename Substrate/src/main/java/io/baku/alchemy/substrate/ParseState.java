package io.baku.alchemy.substrate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import com.google.common.collect.ImmutableList;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.baku.alchemy.substrate.fsa.Edge;
import io.baku.alchemy.substrate.fsa.Fsa;
import io.baku.alchemy.substrate.fsa.Node;
import io.baku.alchemy.substrate.predicates.ParsePredicate;
import io.baku.alchemy.substrate.rules.Rule;
import io.baku.alchemy.substrate.symbols.Symbol;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.Value;

@Value
@ToString(exclude = "context")
@RequiredArgsConstructor
public class ParseState {
    public static enum Phase {
        BEFORE_TOKEN,
        TOKEN,
        AFTER_TOKEN
    }
    
    /**
     * The utility of a parse state is as follows:
     * (cost + depth + input sequence size, matches in progress, entropy)
     * <p>
     * Less is better.
     */
    public static final Comparator<ParseState> UTILITY =
            Comparator.comparing(ParseState::getCostEstimate)
            .thenComparing(s -> s.getErrors().size())
            .thenComparing(s -> s.getInputScanner().getSequence().isEmpty()? 0 :
            	((s.getInputScanner().getCursor() -
            		s.getInputScanner().getCycleStart()) %
            		s.getInputScanner().getSequence().size()));
    
    ParseContext context;
    Scanner<Symbol> inputScanner;
    
    @Nullable Rule pendingRule;
    Phase phase;
    int matchStart, contentStart, contentEnd;
    @Nullable Node fsaNode;
    int errorCost;
    ImmutableList<ParseError> errors;
    float entropy;
    long stateDepth;
    /**
     * Symbol depth, not including predicate graphs within rules
     * ({@code getPendingRule().getGraphWalkState().getDepth()}).
     */
    long symbolDepth;
    long inputDepth;
    
    private static @Nullable Node nodeFromVt(final @Nullable Fsa fsa) {
    	return fsa == null? null : fsa.getStart();
    }
    
    public ParseState(final ParseContext context, final List<? extends Symbol> input) {
    	this(context, new Scanner.Simple<>(ImmutableList.copyOf(input)), null, Phase.BEFORE_TOKEN,
    			0, -1, -1, nodeFromVt(context.getVirtualTokenizer()), 0, ImmutableList.of(), 0, 0,
    			0, 0);
    }
    
    @Value
    public static class StaticState {
        ParseContext context;
        Scanner<Symbol> inputScanner;
        Rule pendingRule;
        int phase, matchStart, contentStart, contentEnd;
        Node fsaNode;
    }
    
    public StaticState getStaticState() {
        return new StaticState(context, inputScanner,
                pendingRule,
                phase.ordinal(),
                matchStart,
                contentStart,
                contentEnd,
                fsaNode);
    }
    
    public float getCostEstimate() {
    	return 25 * errorCost + entropy +
    			inputScanner.getSequence().size() + matchStart - inputScanner.getCursor();
    }
    
    public boolean hasPendingRule() {
    	return pendingRule != null;
    }
    
    public boolean hasPendingFsa() {
        return fsaNode != null;
    }

    private @NonNull ParseState withFailedMatch(final Scanner<Symbol> nextScanner,
    		final int errorCostDelta, final ParseError error, final Node nextNode,
    		final long nextStateDepth) {
        return new ParseState(context,
                nextScanner,
                pendingRule,
                phase,
                matchStart,
                contentStart,
                contentEnd,
                nextNode,
                errorCost + errorCostDelta,
                ImmutableList.<ParseError>builder().addAll(errors).add(error).build(),
                entropy + 1,
                nextStateDepth,
                symbolDepth,
                inputDepth + 1);
    }
    
    private ParseState produce() {
    	// TODO(rosswang): Use some kind of tree list.
        final List<Symbol>
                seq = inputScanner.getSequence(),
                content = seq.subList(contentStart, phase == Phase.AFTER_TOKEN?
                		contentEnd : inputScanner.getCursor()),
                repl = new ArrayList<>(seq.size() - inputScanner.getCursor() + matchStart);
        repl.addAll(seq.subList(0, matchStart));
        repl.add(pendingRule.getProduction().apply(content));
        repl.addAll(seq.subList(inputScanner.getCursor(), seq.size()));
        
        final int newCursor = (matchStart + 1) % repl.size();
        
        return new ParseState(context,
                new Scanner.Simple<>(repl, newCursor),
                null, phase, newCursor, -1, -1,
                null, errorCost, errors, entropy, stateDepth, symbolDepth + 1, inputDepth);
    }

    /**
     * Advances a parse state with a pending rule. Produces a fork with the following
     * possibilities:
     * <ul>
     *  <li>Symmetric consumption (match, or expected A; found B) (per rule)
     *  <li>Rule consumption (expected not found) (per rule)
     *  <li>Input consumption (found not expected) (once)
     * </ul>
     * 
     * @throws NullPointerException if {@code pendingRule} is null
     */
    public @NonNull Collection<ParseState> expand() {
        final ImmutableList.Builder<ParseState> builder = ImmutableList.builder();
        
        if (fsaNode.getEdges().isEmpty()) {
        	final Fsa vt = context.getVirtualTokenizer();
        	if (phase == Phase.TOKEN && vt != null) {
        		builder.add(new ParseState(context, inputScanner, pendingRule, Phase.AFTER_TOKEN,
        				matchStart, contentStart, inputScanner.getCursor(), vt.getStart(),
        				errorCost, errors, entropy, stateDepth, symbolDepth, inputDepth));
        	} else if (phase != Phase.BEFORE_TOKEN) {
        		builder.add(produce());
        	}
        }
        
        for (final Edge e : fsaNode.getEdges()) {
            final ParsePredicate p = e.getPredicate();
            
            if (p == null) {
                //lambda; just add it on
                builder.add(new ParseState(context, inputScanner, pendingRule, phase, matchStart,
                		contentStart, contentEnd, e.getTarget(), errorCost, errors, entropy,
                		stateDepth + 1, symbolDepth, inputDepth));
            } else {
                if (p.test(inputScanner)) {
                    builder.add(new ParseState(context, inputScanner.advance(p.getWidth(), false),
                            pendingRule, phase, matchStart, contentStart, contentEnd,
                            e.getTarget(), errorCost, errors, entropy + p.getEntropy(),
                            stateDepth + 1, symbolDepth, inputDepth + p.getWidth()));
                } else if (inputScanner.hasCurrent()) {
                	// TODO(rosswang): What about errors on lookarounds?
                    builder.add(withFailedMatch(inputScanner.advance(1, false),
                    		3, new ParseError(inputScanner.getCurrent(), p),
                    		e.getTarget(), stateDepth + 1));
                }
                
                // expected, not found
                final Scanner<Symbol> enfScanner = context.getVirtualTokenizer() == null?
                		null : context.getVirtualTokenizer().consume(inputScanner);
                builder.add(withFailedMatch(enfScanner == null? inputScanner : enfScanner,
                		2, new ParseError(null, p), e.getTarget(), stateDepth + 1));
            }
        }
        
        if (inputScanner.hasCurrent()) {
            // found, not expected
            builder.add(withFailedMatch(inputScanner.advance(1, false), 2,
            		new ParseError(inputScanner.getCurrent(), null), fsaNode, stateDepth));
        }
        
        return builder.build();
    }
    
    /**
     * Advances a parse state without a pending rule. The input scanner wraps around.
     */
    public ParseState advance() {
    	final Scanner<Symbol> advanced = inputScanner.advance();
        return new ParseState(context, advanced, null, Phase.BEFORE_TOKEN, advanced.getCursor(),
        		-1, -1, nodeFromVt(context.getVirtualTokenizer()), errorCost, errors, entropy,
        		stateDepth, symbolDepth, inputDepth + 1);
    }

    public @NonNull ParseState withPendingRule(@NonNull final Rule rule) {
        if (hasPendingRule()) {
            throw new IllegalStateException("Cannot set pending rule " + rule + " while rule " +
            		pendingRule + " is pending");
        }
        return new ParseState(context, inputScanner, rule, Phase.TOKEN, matchStart,
        		inputScanner.getCursor(), -1, rule.getFsa().getStart(), errorCost, errors, entropy,
        		stateDepth, symbolDepth, inputDepth);
    }
}
