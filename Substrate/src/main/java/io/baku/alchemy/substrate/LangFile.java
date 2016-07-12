package io.baku.alchemy.substrate;

import io.baku.alchemy.substrate.fsa.Fsa;
import io.baku.alchemy.substrate.predicates.CharPredicate;
import io.baku.alchemy.substrate.predicates.TypePredicate;
import io.baku.alchemy.substrate.rules.Rule;
import io.baku.alchemy.substrate.rules.TextRules;
import io.baku.alchemy.substrate.symbols.Symbol;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LangFile {
    public static final ParseContext PARSE_CONTEXT;
    static {
    	final TypePredicate
    		name = new TypePredicate("name", 1),
    		pattern = new TypePredicate("pattern", 1);
    	final Fsa positiveInteger = new Fsa()
    			.append(CharPredicate.range('1', '9'))
    			.append(new Fsa()
    					.append(CharPredicate.range('0', '9'))
    					.kleeneStar());
    	
    	PARSE_CONTEXT = new ParseContext(
                new Rule("languageElement", new Fsa()
                        .orKeyword("private")
                        .append(new Fsa()
                                .appendType("tokens", 1)
                                .orType("translation", 1))),
                new Rule("tokens", new Fsa()
                		.appendKeyword("tokens")
                		.append(new Fsa()
                				.append(name)
                				.repeat())),
                new Rule("translation", new Fsa()
                		.append(name)
                		.append(':')
                		.append(pattern)
                		.append(new Fsa()
                				.append('=')
                				.appendType("definition", 1)
                				.optional())),
                new Rule("name", CharPredicate.IS_IDENTIFIER),
                new Rule("pattern", new Fsa()
                		.appendType("pattern.prefixQuantifier", 1)
                		.optional()
                		.append(new Fsa()
                				.append(name)
                				.append(':')
                				.optional())
                		.appendType("pattern.disjunction", 1)
                		.append(new Fsa()
                				.append(pattern)
                				.optional())),
                new Rule("pattern.disjunction", new Fsa()
                		.appendType("pattern.term", 1)
                		.repeat('|')),
                new Rule("pattern.term", new Fsa()
                		.append(name)
                		.orType("pattern.list", 1)
                		.orType("pattern.literal", 1)
                		.orType("pattern.regex", 1)
                		.orType("pattern.group", 1)
                		.append(new Fsa()
                				.appendType("pattern.quantifier", 1)
                				.optional())),
                new Rule("pattern.group", new Fsa()
                		.append('(')
                		.append(pattern)
                		.append(')')),
                new Rule("pattern.quantifier", new Fsa()
                		.append('?')
                		.or('+')
                		.or('*')
                		.or(new Fsa()
                				.append('{')
                				.appendType("pattern.quantifier.min", 1)
                				.append(new Fsa()
                						.append('-')
                						.appendType("pattern.quantifier.max", 1)
                						.optional())
                				.append('}'))),
                new Rule("pattern.quantifier.min", new Fsa()
                		.appendType("wholeNumber", 1)),
                new Rule("pattern.quantifier.max", new Fsa()
                		.appendType("positiveInteger", 1)),
                new Rule("wholeNumber",
                		x -> Integer.parseInt(Symbol.stringValue(x)),
                		positiveInteger.clone().or('0')),
                new Rule("positiveInteger",
                		x -> Integer.parseInt(Symbol.stringValue(x)),
                		positiveInteger),
                TextRules.keyword("private"),
                TextRules.keyword("tokens")
            );
    }
}
