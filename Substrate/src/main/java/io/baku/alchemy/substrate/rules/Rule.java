package io.baku.alchemy.substrate.rules;

import java.util.List;
import java.util.function.Function;

import com.google.common.collect.Iterables;

import io.baku.alchemy.substrate.fsa.Fsa;
import io.baku.alchemy.substrate.symbols.Symbol;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class Rule {
    String description;
    Symbol.Production production;
    Fsa fsa;
    
    public Rule(final String description, final Fsa fsa) {
        this.description = description;
        production = x -> new Symbol(description, x);
        this.fsa = fsa;
    }
    
    public Rule(final String description, final Fsa fsa, final Function<List<Symbol>, Object> valueFn) {
        this.description = description;
        production = x -> {
        	Object value;
        	try {
        		value = valueFn.apply(x);
        	} catch (final RuntimeException e) {
        		value = null;
        	}
        	return new Symbol(description, value, x);
        };
        this.fsa = fsa;
    }
    
    @Override
    public String toString() {
        return description;
    }
    
    public static Rule alias(final String newType, final String oldType) {
        return new Rule(newType, new Fsa().appendType(oldType),
                x -> Iterables.getOnlyElement(x).getValue());
    }
}
