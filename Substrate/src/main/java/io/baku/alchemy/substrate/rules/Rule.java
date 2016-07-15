package io.baku.alchemy.substrate.rules;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import com.google.common.collect.Iterables;

import io.baku.alchemy.substrate.ParseContext;
import io.baku.alchemy.substrate.fsa.Fsa;
import io.baku.alchemy.substrate.symbols.Symbol;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class Rule {
    String description;
    Fsa fsa;
    UnaryOperator<List<Symbol>> production;
    UnaryOperator<ParseContext> sideEffects;
    
    public Rule(final String description, final Fsa fsa) {
        this(description, fsa, defaultProduction(description), null);
    }
    
    public Rule(final String description, final Fsa fsa, final Function<List<Symbol>, Object> valueFn) {
        this(description, fsa, valueProduction(description, valueFn), null);
    }
    
    @Override
    public String toString() {
        return description;
    }
    
    public static Rule alias(final String newType, final String oldType) {
        return new Rule(newType, new Fsa().appendType(oldType),
                x -> Iterables.getOnlyElement(x).getValue());
    }
    
    private static UnaryOperator<List<Symbol>> defaultProduction(final String description) {
    	return x -> Collections.singletonList(new Symbol(description, x));
    }
    
    private static UnaryOperator<List<Symbol>> valueProduction(final String description,
    		final Function<List<Symbol>, Object> valueFn) {
    	return x -> {
        	Object value;
        	try {
        		value = valueFn.apply(x);
        	} catch (final RuntimeException e) {
        		value = null;
        	}
        	return Collections.singletonList(new Symbol(description, value, x));
        };
    }
    
    public static Rule withProduction(final String description, final Fsa fsa,
    		final Function<List<Symbol>, Symbol> production) {
    	return new Rule(description, fsa,
    			x -> Collections.singletonList(production.apply(x)), null);
    }
    
    public static Rule withSideEffects(final String description, final Fsa fsa,
    		final UnaryOperator<ParseContext> sideEffects) {
    	return new Rule(description, fsa, defaultProduction(description), sideEffects);
    }
    
    public static Rule withSideEffects(final String description, final Fsa fsa,
    		final Function<List<Symbol>, Object> valueFn,
    		final UnaryOperator<ParseContext> sideEffects) {
    	return new Rule(description, fsa, valueProduction(description, valueFn), sideEffects);
    }
}
