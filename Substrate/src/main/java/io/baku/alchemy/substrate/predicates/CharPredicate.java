package io.baku.alchemy.substrate.predicates;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.baku.alchemy.substrate.symbols.CharSymbol;
import io.baku.alchemy.substrate.symbols.Symbol;

public abstract class CharPredicate extends SymbolPredicate {
    public CharPredicate(final int matchSpace) {
        super(entropy(matchSpace, Character.MAX_CODE_POINT));
    }

    protected abstract boolean test(final int testCodePoint);

    @Override
    protected final boolean test(final Symbol s) {
        return s.getType().equals(CharSymbol.TYPE) &&
                s.testValue(Integer.class, this::test);
    }
    
    public static final int
            WHITESPACE_MATCH_SPACE = 26,
            IDENTIFIER_MATCH_SPACE = 103584;
    
    public static final CharPredicate
            IS_WHITESPACE = CharPredicate.createCharPredicate(WHITESPACE_MATCH_SPACE,
                    Character::isWhitespace),
            IS_IDENTIFIER = CharPredicate.createCharPredicate(IDENTIFIER_MATCH_SPACE,
                    Character::isJavaIdentifierPart);

    public static CharPredicate matching(final int codePoint) {
        return new CharPredicate(1) {
            @Override
            protected boolean test(final int testCodePoint) {
                return testCodePoint == codePoint;
            }
            
            @Override
            public String toString() {
                return new StringBuilder("'")
                		.append(Character.toChars(codePoint))
                		.append('\'')
                		.toString();
            }
        };
    }

    public static CharPredicate createCharPredicate(final int matchSpace, final Predicate<Integer> fn) {
        return new CharPredicate(matchSpace) {
            @Override
            protected boolean test(final int testCodePoint) {
                return fn.test(testCodePoint);
            }
        };
    }

    public static CharPredicate anyOf(final String c) {
        final Set<Integer> codePoints = c.codePoints()
                .boxed()
                .collect(Collectors.toSet());
        return new CharPredicate(codePoints.size()) {
            @Override
            protected boolean test(final int testCodePoint) {
                return codePoints.contains(testCodePoint);
            }
            
            @Override
            public String toString() {
                return "[" + c + "]";
            }
        };
    }
    
    /**
     * @param maxCodePoint the maximal code point to accept, inclusive
     */
    public static CharPredicate range(final int minCodePoint, final int maxCodePoint) {
        return new CharPredicate(maxCodePoint - minCodePoint + 1) {
            @Override
            protected boolean test(int testCodePoint) {
                return testCodePoint >= minCodePoint && testCodePoint <= maxCodePoint;
            }
            
            @Override
            public String toString() {
                return new StringBuilder("[")
                        .append(Character.toChars(minCodePoint))
                        .append('-')
                        .append(Character.toChars(maxCodePoint))
                        .append(']')
                        .toString();
            }
        };
    }
}
