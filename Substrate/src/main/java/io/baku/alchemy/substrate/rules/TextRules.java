package io.baku.alchemy.substrate.rules;

import io.baku.alchemy.substrate.fsa.Fsa;
import io.baku.alchemy.substrate.symbols.KeywordSymbol;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TextRules {
    public static Rule keyword(final String keyword) {
        return new Rule("keyword: " + keyword,
                x -> new KeywordSymbol(keyword, x),
                new Fsa().append(keyword));
    }
    
    public static Rule keyword(final String keyword, final Object value) {
        return new Rule("keyword: " + keyword, x -> value, new Fsa().append(keyword));
    }
}
