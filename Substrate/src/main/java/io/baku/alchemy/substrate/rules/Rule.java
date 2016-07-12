package io.baku.alchemy.substrate.rules;


import java.util.List;
import java.util.function.Function;

import io.baku.alchemy.substrate.fsa.Fsa;
import io.baku.alchemy.substrate.symbols.KeywordSymbol;
import io.baku.alchemy.substrate.symbols.Symbol;
import lombok.Value;

@Value
public class Rule {
    String name;
    Symbol.Production production;
    Fsa fsa;
    
    public Rule(final String name, final Fsa fsa) {
        this.name = name;
        production = x -> new Symbol(name, x);
        this.fsa = fsa;
    }
    
    public Rule(final String name, final Function<List<Symbol>, Object> valueFn, final Fsa fsa) {
        this.name = name;
        production = x -> {
        	Object value;
        	try {
        		value = valueFn.apply(x);
        	} catch (final RuntimeException e) {
        		value = null;
        	}
        	return new Symbol(name, value, x);
        };
        this.fsa = fsa;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public static Rule keyword(final String keyword) {
        return new Rule("keyword: " + keyword,
                x -> new KeywordSymbol(keyword, x),
                new Fsa().append(keyword));
    }
}
