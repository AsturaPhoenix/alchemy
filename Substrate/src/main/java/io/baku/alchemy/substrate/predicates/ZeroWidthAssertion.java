package io.baku.alchemy.substrate.predicates;

import io.baku.alchemy.substrate.Scanner;
import io.baku.alchemy.substrate.fsa.Fsa;
import io.baku.alchemy.substrate.symbols.Symbol;

public class ZeroWidthAssertion extends ParsePredicate {
    private final boolean positive, lookahead;
    private final Fsa nested;
    
    public ZeroWidthAssertion(final boolean positive, final boolean lookahead, final Fsa nested) {
        super(0, 0); // TODO(rosswang): variable entropy; derive from FSA match
        this.positive = positive;
        this.lookahead = lookahead;
        this.nested = nested;
    }
    
    public ZeroWidthAssertion(final boolean positive, final boolean lookahead, final ParsePredicate predicate) {
        this(positive, lookahead, new Fsa().append(predicate));
    }

    @Override
    public boolean test(Scanner<? extends Symbol> state) {
        return nested.accepts(lookahead? state : state.reverse()) == positive;
    }
    
    public static ZeroWidthAssertion negativeLookbehind(final ParsePredicate predicate) {
        return new ZeroWidthAssertion(false, false, predicate);
    }
    
    public static ZeroWidthAssertion negativeLookahead(final ParsePredicate predicate) {
        return new ZeroWidthAssertion(false, true, predicate);
    }
    
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("?");
        if (!lookahead) {
            builder.append('<');
        }
        builder.append(positive? '=' : '!')
                .append(nested);
        return builder.toString();
    }
}
