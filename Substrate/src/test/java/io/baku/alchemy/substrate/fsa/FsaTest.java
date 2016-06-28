package io.baku.alchemy.substrate.fsa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.baku.alchemy.substrate.ParseContext;
import io.baku.alchemy.substrate.Scanner;
import io.baku.alchemy.substrate.predicates.CharPredicate;
import io.baku.alchemy.substrate.predicates.TypePredicate;
import io.baku.alchemy.substrate.predicates.ZeroWidthAssertion;
import io.baku.alchemy.substrate.symbols.CharSymbol;
import io.baku.alchemy.substrate.symbols.Symbol;

public class FsaTest {
    @Test
    public void testEmpty() {
        final Fsa fsa = new Fsa()
                .eof();
        
        assertTrue(fsa.accepts(""));
        assertFalse(fsa.accepts("more"));
    }
    
    @Test
    public void testSimple() {
        final Fsa fsa = new Fsa()
            .append("hello")
            .eof();
        
        assertFalse(fsa.accepts(""));
        assertFalse(fsa.accepts("hellish"));
        assertTrue(fsa.accepts("hello"));
        assertFalse(fsa.accepts("hello, world!"));
    }

    @Test
    public void testOr() {
        final Fsa fsa = new Fsa()
                .append("hello")
                .or("world")
                .eof();
        
        assertFalse(fsa.accepts(""));
        assertFalse(fsa.accepts("no"));
        assertTrue(fsa.accepts("hello"));
        assertTrue(fsa.accepts("world"));
    }

    @Test
    public void testRepeating() {
        final Fsa fsa = new Fsa()
                .append("hi")
                .kleeneStar()
                .eof();
        
        assertTrue(fsa.accepts(""));
        assertFalse(fsa.accepts("h"));
        assertFalse(fsa.accepts("hih"));
        assertFalse(fsa.accepts("ihi"));
        assertTrue(fsa.accepts("hi"));
        assertTrue(fsa.accepts("hihi"));
        assertTrue(fsa.accepts("hihihi"));
    }
    
    @Test
    public void testGreediness() {
    	final Fsa fsa = new Fsa()
    			.append("hi");
    	
    	final Scanner<? extends Symbol> scanner = new Scanner.Simple<>(
    			CharSymbol.fromString("hihihello"));
    	
    	assertEquals(2, fsa.repeat().consume(scanner).getCursor());
    	assertEquals(4, fsa.repeat(null, true).consume(scanner).getCursor());
    }
    
    @Test
    public void testPrefix() {
        final Fsa fsa = new Fsa()
                .append("Hello");
        
        assertFalse(fsa.accepts("Hell"));
        assertFalse(fsa.accepts("Why, Hello"));
        assertTrue(fsa.accepts("Hello"));
        assertTrue(fsa.accepts("Hello, world!"));
    }
    
    @Test
    public void testZeroWidthNegativeLookahead() {
        final Fsa fsa = new Fsa()
                .append("Hello, ")
                .append(new ZeroWidthAssertion(false, true,
                        new Fsa().append("Goodbye")))
                .append(new Fsa()
                        .append(new TypePredicate(CharSymbol.TYPE, 1))
                        .repeat())
                .append("!")
                .eof();
        
        assertFalse(fsa.accepts("Hello!"));
        assertTrue(fsa.accepts("Hello, world!"));
        assertFalse(fsa.accepts("Hello, Cleveland."));
        assertFalse(fsa.accepts("Hello, Seattle! I am a mountaineer."));
        assertTrue(fsa.accepts("Hello, Seattle! I am a manta ray!"));
        assertFalse(fsa.accepts("Hello, Goodbye"));
        assertFalse(fsa.accepts("Hello, Goodbye!"));
        assertFalse(fsa.accepts("Hello, Goodbye forever!"));
        assertFalse(fsa.accepts("Hello, Goodbye! Goodbye forever now."));
        assertTrue(fsa.accepts("Hello, Goodyville!"));
    }
    
    @Test
    public void testNestedOr() {
        final Fsa fsa = new Fsa()
                .append("v")
                .append(new Fsa()
                        .append("en")
                        .or("id")
                        .or("ic"))
                .append("i")
                .append(new Fsa()
                        .append(", ").optional())
                .repeat()
                .eof();
        
        assertTrue(fsa.accepts("veni, vidi, vici"));
        assertFalse(fsa.accepts("vi"));
    }
    
    @Test
    public void testNestedPredicateOr() {
        final Fsa fsa = new Fsa()
                .append("Fine ")
                .append(new Fsa()
                        .append(CharPredicate.matching('c'))
                        .or(CharPredicate.matching('h'))
                        .append("at"))
                .append("!")
                .eof();
        
        assertTrue(fsa.accepts("Fine cat!"));
        assertTrue(fsa.accepts("Fine hat!"));
        assertFalse(fsa.accepts("Fine bat!"));
        assertFalse(fsa.accepts("Fine at!"));
    }
    
    @Test
    public void testClone() {
        final Fsa
                orig = new Fsa()
                .append("ei")
                .repeat()
                .append("o"),
                clone = orig.clone();
        
        orig.append("!");
        clone.eof();
        
        assertTrue(orig.accepts("eieio!"));
        assertTrue(clone.accepts("eieio"));
    }
    
    @Test
    public void testTrailingZeroWidthNegative() {
        Fsa fsa = new Fsa()
                .append("Hello")
                .append(ParseContext.DEFAULT_VIRTUAL_TOKENIZER);
        
        assertTrue(fsa.accepts("Hello"));
        assertTrue(fsa.accepts("Hello, world!"));
        assertFalse(fsa.accepts("Hellooo"));
        
        fsa = new Fsa()
                .append('}')
                .append(ParseContext.DEFAULT_VIRTUAL_TOKENIZER);
        
        assertTrue(fsa.accepts("}"));
        assertTrue(fsa.accepts("} "));
        assertTrue(fsa.accepts("}\n"));
    }
}
