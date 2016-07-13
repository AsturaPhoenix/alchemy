package io.baku.alchemy.substrate.predicates;

import io.baku.alchemy.substrate.symbols.Symbol;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = false)
public class TypePredicate extends SymbolPredicate {
    private final String type;
    
    public TypePredicate(final String type) {
        super(0);
        this.type = type;
    }

    @Override
    protected boolean test(final Symbol s) {
        return s.getType().equals(type);
    }
    
    @Override
    public String toString() {
        return type;
    }
}
