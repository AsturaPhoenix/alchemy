package io.baku.alchemy.substrate.symbols;

import java.util.List;

public class KeywordSymbol extends Symbol {
    public static final String TYPE = "keyword";
    
    public KeywordSymbol(final String keyword, final List<Symbol> children) {
        super(TYPE, keyword, children);
    }
}
