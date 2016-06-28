package io.baku.alchemy.substrate.predicates;

import static org.junit.Assert.assertEquals;

import java.util.function.Predicate;

import org.junit.Test;

public class CharPredicateTest {
    private static int countMatches(final Predicate<Integer> fn) {
        int matchSpace = 0;
        for (int i = Character.MIN_CODE_POINT; i <= Character.MAX_CODE_POINT; i++) {
            if (fn.test(i)) {
                matchSpace++;
            }
        }
        return matchSpace;
    }

    // It is not super critical that these constants remain accurate, so just hard-code the
    // approximate value and have unit tests to warn of any discrepancies.
    
    @Test
    public void testWhitespaceMatchSpace() {
        assertEquals(CharPredicate.WHITESPACE_MATCH_SPACE, countMatches(Character::isWhitespace));
    }

    @Test
    public void testIdentifierMatchSpace() {
        assertEquals(CharPredicate.IDENTIFIER_MATCH_SPACE, countMatches(Character::isJavaIdentifierPart));
    }
}
