package io.baku.alchemy.substrate.rules;

import io.baku.alchemy.substrate.fsa.Fsa;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TextRules {
    public static Rule keyword(final String keyword) {
        return new Rule(keyword, new Fsa().append(keyword));
    }
    
    public static Rule keyword(final String keyword, final Object value) {
        return new Rule(keyword, x -> value, new Fsa().append(keyword));
    }
}
