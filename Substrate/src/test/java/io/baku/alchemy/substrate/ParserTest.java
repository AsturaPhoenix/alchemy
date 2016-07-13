package io.baku.alchemy.substrate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;

import io.baku.alchemy.substrate.Parser.ParseResult;
import io.baku.alchemy.substrate.fsa.Fsa;
import io.baku.alchemy.substrate.predicates.CharPredicate;
import io.baku.alchemy.substrate.predicates.ParsePredicate;
import io.baku.alchemy.substrate.rules.Rule;
import io.baku.alchemy.substrate.rules.TextRules;
import io.baku.alchemy.substrate.symbols.Symbol;

public class ParserTest {
	private static final ParseContext JSON;
	static {
		 final ParsePredicate
		         normalChar = CharPredicate.createCharPredicate(Character.MAX_CODE_POINT - 32,
		                 c -> c != '"' && c != '\\' && (c == '\t' || c >= 32)),
		         digit = CharPredicate.range('0', '9');
		
		 final Fsa
		         escapeSeq = new Fsa()
		                 .append('\\')
		                 .append(CharPredicate.anyOf("\"\\/bfnrt")),
		         digits = new Fsa()
		                 .append(digit)
		                 .repeat();
		 
		 JSON = new ParseContext(
		         new Rule("string", new Fsa()
		                 .append('"')
		                 .append(new Fsa()
		                         .append(normalChar)
		                         .or(escapeSeq)
		                         .kleeneStar())
		                 .append('"'), Symbol::stringValue),
		         new Rule("number", new Fsa()
		                 .or('-')
		                 .append(CharPredicate.range('1', '9'))
		                 .append(digits)
		                 .or(digit)
		                 .append(new Fsa()
		                         .append('.')
		                         .append(digits)
		                         .optional())
		                 .append(new Fsa()
		                         .append(CharPredicate.anyOf("eE"))
		                         .append(new Fsa()
		                                 .append(CharPredicate.anyOf("+-"))
		                                 .optional())
		                         .append(digits)
		                         .optional()), x -> Double.parseDouble(Symbol.stringValue(x))),
		         new Rule("value", new Fsa()
		                 .appendType("string")
		                 .orType("number")
		                 .orType("object")
		                 .orType("array")
		                 .orKeyword("true")
		                 .orKeyword("false")
		                 .orKeyword("null"), x -> x.get(0).getValue()),
		         new Rule("entry", new Fsa()
		                 .appendType("string")
		                 .append(':')
		                 .appendType("value")),
		         new Rule("object", new Fsa().fencedList('{', "entry", ',', '}')),
		         new Rule("array", new Fsa().fencedList('[', "value", ',', ']')),
		         TextRules.keyword("true", true),
		         TextRules.keyword("false", false),
		         TextRules.keyword("null"));
	}
	
	private static String read(final String resource) throws IOException {
		return new String(ByteStreams.toByteArray(ParserTest.class.getResourceAsStream(resource)));
	}
	
	private static ParseResult testParse(final ParseContext context, final String resource, final String startSymbol) throws IOException {
		final ParseResult result = Parser.parse(context, read(resource), startSymbol);

        System.out.println("Errors: " + result.getErrors());
        System.out.println(result.getStartSymbol().printTree());

        assertEquals(startSymbol, result.getStartSymbol().getType());
        
		return result;
	}
	
	private static void testParseSuccessful(final ParseContext context, final String resource,
			final String startSymbol) throws IOException {
        final ParseResult result = testParse(context, resource, startSymbol);
        assertTrue(result.getErrors().isEmpty());
	}
	
	private static void testParseErrors(final ParseContext context, final String resource,
			final String startSymbol, final String... errors) throws IOException {
		final ParseResult result = testParse(context, resource, startSymbol);
		assertEquals(ImmutableList.copyOf(errors),
				Lists.transform(result.getErrors(), Object::toString));
	}
	
	@Test
	public void testParseEmptyJson() throws IOException {
		testParseSuccessful(JSON, "emptyobject.json", "object");
	}
	
	@Test
	public void testParseSimpleJson() throws IOException {
		testParseSuccessful(JSON, "simpleobject.json", "object");
	}
	
    @Test
    public void testParseJson() throws IOException {
        testParseSuccessful(JSON, "object.json", "object");
    }
	
	@Test
	public void testParseCompletelyEmptyJson() throws IOException {
		testParseErrors(JSON, "emptyerror.json", "object", "Expected '{'", "Expected '}'");
	}
	
	@Test
	public void testParseEmptyWithSimpleError() throws IOException {
		testParseErrors(JSON, "emptysimpleerror.json", "object", "Expected '{'");
	}
	
	@Test
	public void testParseObjectMissingValue() throws IOException {
		testParseErrors(JSON, "objectmissingvalue.json", "object", "Expected value");
	}
}
