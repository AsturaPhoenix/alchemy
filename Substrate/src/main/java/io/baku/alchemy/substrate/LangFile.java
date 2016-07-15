package io.baku.alchemy.substrate;

import com.google.common.collect.Range;

import io.baku.alchemy.substrate.fsa.Fsa;
import io.baku.alchemy.substrate.predicates.CharPredicate;
import io.baku.alchemy.substrate.rules.Rule;
import io.baku.alchemy.substrate.rules.TextRules;
import io.baku.alchemy.substrate.symbols.Symbol;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LangFile {
    public static final ParseContext PARSE_CONTEXT;
    static {
    	final Fsa positiveInteger = new Fsa()
    			.append(CharPredicate.range('1', '9'))
    			.append(new Fsa()
    					.append(CharPredicate.range('0', '9'))
    					.kleeneStar());
    	
    	PARSE_CONTEXT = new ParseContext(
    			ParseContext.virtualTokenizer(new Fsa()
    					.append(CharPredicate.IS_WHITESPACE)
    					.orType("comment")),
                new Rule("languageElement", new Fsa()
                        .orKeyword("private")
                        .append(new Fsa()
                                .appendType("tokens")
                                .orType("translation"))),
                new Rule("tokens", new Fsa()
                		.appendKeyword("tokens")
                		.append(new Fsa()
                				.appendType("name")
                				.repeat())),
                new Rule("translation", new Fsa()
                		.appendType("name")
                		.append(':')
                		.appendType("pattern")
                		.append(new Fsa()
                				.append('=')
                				.appendType("definition")
                				.optional())),
                new Rule("name", CharPredicate.IS_IDENTIFIER),
                new Rule("pattern", new Fsa()
                		.appendType("pattern.prefixQuantifier")
                		.optional()
                		.append(new Fsa()
                				.appendType("name")
                				.append(':')
                				.optional())
                		.appendType("pattern.disjunction")
                		.append(new Fsa()
                				.appendType("pattern")
                				.optional())),
                new Rule("pattern.disjunction", new Fsa()
                		.appendType("pattern.term")
                		.repeat('|')),
                new Rule("pattern.term", new Fsa()
                		.appendType("name")
                		.orType("pattern.list")
                		.orType("pattern.literal")
                		.orType("pattern.regex")
                		.orType("pattern.group")
                		.append(new Fsa()
                				.appendType("pattern.quantifier")
                				.optional())),
                new Rule("pattern.group", new Fsa()
                		.append('(')
                		.appendType("pattern")
                		.append(')')),
                new Rule("pattern.quantifier", new Fsa()
                		.append('?')
                		.or('+')
                		.or('*')
                		.or(new Fsa()
                				.append('{')
                				.appendType("pattern.quantifier.min")
                				.append(new Fsa()
                						.append('-')
                						.appendType("pattern.quantifier.max")
                						.optional())
                				.append('}')),
                		x -> {
                			switch(Symbol.stringValue(x).charAt(0)) {
                			case '?':
                				return Range.closed(0, 1);
                			case '+':
                				return Range.atLeast(1);
                			case '*':
                				return Range.atLeast(0);
            				default:
            					final Symbol min = Symbol.getByType(x, "pattern.quantifier.min");
                				final Symbol max = Symbol.getByType(x,
                						"pattern.quantifier.max", min);
                				return Range.closed((int)min.getValue(), (int)max.getValue());
                			}
                		}),
                Rule.alias("pattern.quantifier.min", "wholeNumber"),
                Rule.alias("pattern.quantifier.max", "positiveInteger"),
                new Rule("pattern.prefixQuantifier", new Fsa()
                		.appendType("pattern.quantifier")
                		.append('>')),
                new Rule("wholeNumber", positiveInteger.clone().or('0'),
                		x -> Integer.parseInt(Symbol.stringValue(x))),
                new Rule("positiveInteger", positiveInteger,
                		x -> Integer.parseInt(Symbol.stringValue(x))),
                TextRules.keyword("private"),
                TextRules.keyword("tokens")
            );
    }
}
