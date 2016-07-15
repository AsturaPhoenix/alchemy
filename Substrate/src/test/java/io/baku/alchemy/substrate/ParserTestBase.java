package io.baku.alchemy.substrate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;

import io.baku.alchemy.substrate.Parser.ParseResult;

public class ParserTestBase {
	public static String read(final String resource) throws IOException {
		return new String(ByteStreams.toByteArray(ParserTestBase.class.getResourceAsStream(resource)));
	}
	
	public static ParseResult testParse(final ParseContext context, final String resource, final String startSymbol) throws IOException {
		final ParseResult result = Parser.parse(context, read(resource), startSymbol);

        System.out.println("Errors: " + result.getErrors());
        System.out.println(result.getStartSymbol().printTree());

        assertEquals(startSymbol, result.getStartSymbol().getType());
        
		return result;
	}
	
	public static void testParseSuccessful(final ParseContext context, final String resource,
			final String startSymbol) throws IOException {
        final ParseResult result = testParse(context, resource, startSymbol);
        assertTrue(result.getErrors().isEmpty());
	}
	
	public static void testParseErrors(final ParseContext context, final String resource,
			final String startSymbol, final String... errors) throws IOException {
		final ParseResult result = testParse(context, resource, startSymbol);
		assertEquals(ImmutableList.copyOf(errors),
				Lists.transform(result.getErrors(), Object::toString));
	}
}
