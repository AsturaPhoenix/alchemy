package io.baku.alchemy.substrate.predicates;

import java.util.function.Predicate;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.baku.alchemy.substrate.Scanner;
import io.baku.alchemy.substrate.symbols.Symbol;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class ParsePredicate {
    public static final ParsePredicate EOF = create(0, 0, s -> !s.hasCurrent());
    
    public static ParsePredicate create(final int width, final float entropy,
            final Predicate<? super Scanner<? extends Symbol>> fn) {
        return new ParsePredicate(width, entropy) {
            @Override
            public boolean test(final @NonNull Scanner<? extends Symbol> state) {
                return fn.test(state);
            }
        };
    }
    
    private final int width;
    private final float entropy;
    public abstract boolean test(@NonNull Scanner<? extends Symbol> state);
}
