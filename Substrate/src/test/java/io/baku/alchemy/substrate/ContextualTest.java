package io.baku.alchemy.substrate;

import java.io.IOException;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

import io.baku.alchemy.substrate.fsa.Fsa;
import io.baku.alchemy.substrate.predicates.CharPredicate;
import io.baku.alchemy.substrate.predicates.ZeroWidthAssertion;
import io.baku.alchemy.substrate.rules.Rule;
import io.baku.alchemy.substrate.symbols.Symbol;
import lombok.Value;

public class ContextualTest extends ParserTestBase {
	private static final ParseContext LIST;
	static {
		final Fsa eol = new Fsa()
			.append(' ')
			.kleeneStar()
			.append(new Fsa()
					.append('\n')
					.or('\r'));
		
		LIST = new ParseContext(
			ParseContext.virtualTokenizer(eol),
			listLevel(0),
			new Rule("label", new Fsa()
					.append(CharPredicate.createCharPredicate(Character.MAX_CODE_POINT - 3,
							c -> c != ' ' && c != '\n' && c != '\r'))
					.append(new Fsa()
							.append(new ZeroWidthAssertion(false, true, eol))
							.appendType("character")
							.kleeneStar()),
					Symbol::stringValue));
	}
	
	@Value
	private static class Node {
		String label;
		ImmutableList<Node> children;
	}
	
	@SuppressWarnings("unchecked")
	private static Rule listLevel(final int level) {
		final Fsa fsa = new Fsa();
		for (int i = 0; i < level; i++) {
			fsa.append("    ");
		}
		fsa.appendType("label")
			.appendType("children");
		return new Rule("entry", fsa,
				x -> new Node(
						(String)Symbol.getByType(x, "label").getValue(),
						(ImmutableList<Node>)Symbol.getByType(x, "children").getValue()));
	}
	
	@Test
	public void testParseHierarchy() throws IOException {
		testParseSuccessful(LIST, "hierarchy.list", "object");
	}
}
