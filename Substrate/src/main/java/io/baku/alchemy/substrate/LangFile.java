package io.baku.alchemy.substrate;

import io.baku.alchemy.substrate.fsa.Fsa;
import io.baku.alchemy.substrate.predicates.CharPredicate;
import io.baku.alchemy.substrate.rules.Rule;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LangFile {
    public static final ParseContext PARSE_CONTEXT = new ParseContext(
                ParseContext.virtualTokenizer(CharPredicate.anyOf(" \t")),
                new Rule("languageElement", new Fsa()
                        .orKeyword("private")
                        .append(new Fsa()
                                .appendType("tokens", 1)
                                .orType("translation", 1))),
                Rule.keyword("private"),
                Rule.keyword("tokens")
            );
}
