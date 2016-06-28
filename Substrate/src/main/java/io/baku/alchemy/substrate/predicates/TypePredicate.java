package io.baku.alchemy.substrate.predicates;

import io.baku.alchemy.substrate.symbols.Symbol;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = false)
public class TypePredicate extends SymbolPredicate {
    private final String type;
    
    /**
     * @param entropy the entropy of this type match, typically 0 for semantic types and 1 for
     *  value types. {@code TypePredicate}s for the same type should have the same entropy.
     */
    public TypePredicate(final String type, final float entropy) {
        super(entropy);
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
