package io.baku.alchemy.substrate;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import io.baku.alchemy.substrate.ParseState.Phase;
import io.baku.alchemy.substrate.rules.Rule;
import io.baku.alchemy.substrate.symbols.CharSymbol;
import io.baku.alchemy.substrate.symbols.Symbol;
import lombok.Value;

public class Parser {
    @Value
    public static class ParseResult {
        Symbol startSymbol;
        ImmutableList<ParseError> errors;
    }
    
    private static final long MAX_ERRORS = 256;
    
    public Symbol parse(final File source) {
        return null;
    }
    
    public static ParseResult parse(final ParseContext context, final List<? extends Symbol> input,
            final Predicate<? super Symbol> startSymbol) {
        final PriorityQueue<ParseState> queue = new PriorityQueue<>(ParseState.UTILITY);
        final Set<ParseState.StaticState> seen = new HashSet<>(); 
        
        queue.add(new ParseState(context, input));
        
        while(!queue.isEmpty()) {
            final ParseState state = queue.remove();
            
            final ParseState.StaticState staticState = state.getStaticState();
            if (!seen.add(staticState)) {
                continue;
            }
            
            /*System.out.println(state.getCostEstimate() + "; " + state.getErrors().size() + "-" + state.getInputDepth() + "/" + state.getEntropy()
            	+ "@" + state.getMatchStart()
            	+ ": " + (state.hasPendingRule()? state.getPendingRule().getName() + " - " : "") + state.getInputScanner());*/
            
            if (state.getErrors().size() > MAX_ERRORS) {
                throw new IllegalArgumentException("Too many errors");
            }
            
            if (state.hasPendingFsa()) {
                queue.addAll(state.expand());
            }
            
            if (!state.hasPendingFsa() || (state.getPhase() == Phase.BEFORE_TOKEN &&
            		state.getFsaNode().getEdges().isEmpty())) {
                for (final Rule rule : state.getContext().getRules()) {
                    queue.add(state.withPendingRule(rule));
                }
                
                final ParseState iter = state.advance();
                if (iter.getInputScanner().getCursor() != iter.getInputScanner().getCycleStart()) {
                    queue.add(iter);
                } else {
                    final List<Symbol> symbols = iter.getInputScanner().getSequence();
                    if (symbols.size() == 1) {
                        final Symbol symbol = Iterables.getOnlyElement(symbols);
                        if (startSymbol.test(symbol)) {
                            return new ParseResult(symbol, iter.getErrors());
                        }
                    }
                }
            }
        }
        
        throw new IllegalArgumentException("No route to start symbol");
    }
    
    public static ParseResult parse(final ParseContext context, final String input, final String startSymbolType) {
        return parse(context, CharSymbol.fromString(input), s -> s.getType().equals(startSymbolType));
    }
}
