package io.baku.alchemy.substrate.predicates;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.baku.alchemy.substrate.ParseState;
import io.baku.alchemy.substrate.Scanner;
import io.baku.alchemy.substrate.symbols.Symbol;

/**
 * Convenience subclass that tests only the current symbol of a {@link ParseState}.
 */
public abstract class SymbolPredicate extends ParsePredicate {
    public static float entropy(final int matchSpace, final int domain) {
        return (float)(Math.log(matchSpace) / Math.log(domain) / 5);
    }
    
    public SymbolPredicate(final float entropy) {
        super(1, entropy);
    }
    
    protected abstract boolean test(Symbol s);
    
    @Override
    public final boolean test(final @NonNull Scanner<? extends Symbol> state) {
        return state.hasCurrent() && test(state.getCurrent());
    }
}
