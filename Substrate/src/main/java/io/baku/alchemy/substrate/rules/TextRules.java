package io.baku.alchemy.substrate.rules;

import io.baku.alchemy.substrate.fsa.Fsa;
import io.baku.alchemy.substrate.symbols.KeywordSymbol;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TextRules {
    public static Rule keyword(final String keyword) {
        return Rule.withProduction("keyword: " + keyword, new Fsa().append(keyword),
                x -> new KeywordSymbol(keyword, keyword, x));
    }
    
    public static Rule keyword(final String keyword, final Object value) {
        return Rule.withProduction("keyword: " + keyword, new Fsa().append(keyword),
                x -> new KeywordSymbol(keyword, value, x));
    }
}
