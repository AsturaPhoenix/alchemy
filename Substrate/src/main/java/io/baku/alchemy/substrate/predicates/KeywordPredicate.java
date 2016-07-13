package io.baku.alchemy.substrate.predicates;

import java.util.Objects;

import io.baku.alchemy.substrate.symbols.KeywordSymbol;
import io.baku.alchemy.substrate.symbols.Symbol;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = false)
public class KeywordPredicate extends SymbolPredicate {
    private final String keyword;
    
    public KeywordPredicate(final String keyword) {
        super(0);
        this.keyword = keyword;
    }

    @Override
    protected boolean test(final Symbol s) {
        return s.getType().equals(KeywordSymbol.TYPE) && keyword.equals(((KeywordSymbol)s).getKeyword());
    }
    
    @Override
    public String toString() {
        return Objects.toString(keyword);
    }
}
