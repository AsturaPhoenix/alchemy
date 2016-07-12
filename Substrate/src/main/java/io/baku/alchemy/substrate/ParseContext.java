package io.baku.alchemy.substrate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import io.baku.alchemy.substrate.fsa.Fsa;
import io.baku.alchemy.substrate.predicates.CharPredicate;
import io.baku.alchemy.substrate.predicates.ZeroWidthAssertion;
import io.baku.alchemy.substrate.rules.Rule;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class ParseContext {
    public static final Fsa DEFAULT_VIRTUAL_TOKENIZER = virtualTokenizer(CharPredicate.IS_WHITESPACE);
    
    public static Fsa virtualTokenizer(final CharPredicate whitespace) {
        final Fsa negTerm = new Fsa()
                .append(CharPredicate.IS_IDENTIFIER)
                .or(whitespace);
        return new Fsa()
                .append(CharPredicate.IS_WHITESPACE).repeat(null, true)
                .or(new ZeroWidthAssertion(false, true, negTerm))
                .or(new ZeroWidthAssertion(false, false, negTerm)); 
    }
    
    /**
     * FSA automatically added before and after every rule. This typically includes whitespace
     * consumption and zero-width token boundary testing. 
     */
    Fsa virtualTokenizer;
    Set<Rule> rules;
    
    public ParseContext(final Fsa virtualTokenizer, final Rule... rules) {
        this(virtualTokenizer, new HashSet<>(Arrays.asList(rules)));
    }
    
    public ParseContext(final Rule... rules) {
        this(DEFAULT_VIRTUAL_TOKENIZER, rules);
    }
}
