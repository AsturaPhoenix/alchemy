package io.baku.alchemy.substrate.symbols;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CharSymbolTest {
	@Test
	public void testToString() {
		assertEquals("a", new CharSymbol('a').toString());
		assertEquals(" ", new CharSymbol(' ').toString());
		assertEquals("\\n", new CharSymbol('\n').toString());
		assertEquals("\\u00ab\\ucdef", new CharSymbol(0xabcdef).toString());
	}
}
