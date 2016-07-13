package io.baku.alchemy.substrate.symbols;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class KeywordSymbol extends Symbol {
    public static final String TYPE = "keyword";
    
    String keyword;
    
    public KeywordSymbol(final String keyword, final Object value, final List<Symbol> children) {
        super(TYPE, value, children);
        this.keyword = keyword;
    }
}
