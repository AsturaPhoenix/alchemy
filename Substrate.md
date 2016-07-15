#Substrate Parser

##Overview
Substrate is the base extensible language for [Alchemy](README.md) (see also [Alchemy Milestones](Milestones.md)). It requires a fully dynamic abstract parser capable of defining new rules and removing old rules on the fly while parsing.

##Scenarios
###Rewriting rules
A straightforward way to implement language extensions that can reuse existing compiler infrastructure and transpile into vanilla code is using rewriting rules to translate the extension parses into a symbol sequence compatible with the original language. Rule sets can then be selectively applied to translate the language as desired.

For example, to compile the extended language with the compiler for the original language, the extension rules would first be applied to produce a symbol sequence compatible with the original language. This symbol sequence can then be re-expanded if necessary into a character sequence in the original language.

This re-expansion phase reasonably requires that the compatibility of the rewritten symbol with the original language extend to the entire parse subtree rooted at that symbol. That is, for example, if a rewrite rule produces an “addition” expression symbol in infix math languages, it had better also produce compatible “left operand” and “right operand” subtrees. Re-expansion is not necessary if the rewrite produces a raw character sequence (or similar primitive sequence).

This begs the question of how the same parse can be used for syntax highlighting in the extended language, if the produced symbol tree is in the original language. One reasonable approach is to encourage recognizer rules for the extended language that can guide syntax highlighting prior to rewriting into the original language symbol sequence. Another approach, which could also be used to supplement that approach, might be to use a separate system for syntax highlighting, which may use the ongoing parse as a hint based on coloring rules (e.g. a 0-entropy identifier match outside a comment is a keyword; bold and blue).

The [Markov algorithm](https://en.wikipedia.org/wiki/Markov_algorithm) is a straightforward way to consistently handle rewrite rules. However, it needs some modification to streamline common parse context transformations (see [context hierarchies](#context-hierarchies)) and to offer sane fault tolerance. This is a form of backtracking bottom-up parse.

It is less straightforward to get top-down parsing to handle rewriting rules since top-down parsers are guided by a map of productions to patterns. In the case of rewriting rules, these productions can potentially be expansions or dynamic, which would need to be matched against the pending production in a top-down parse. This is doable, but involves a potential inverse function/production analysis, or may turn into a hybrid top-down-bottom-up parse.

###Retroactive syntax
It is conceivable that one might wish to define custom syntax in two files that are mutually imported from one another, and expect the syntax defined in each import to be available after the import. This may be equivalent to being able to use syntax in a file at a point ahead of its definition.

This is particularly difficult to handle when parsing top-down. Top-down approaches face a similar problem in semantic analysis of programs (e.g. C/C++ prototypes), which is solvable by using symbol tables and multi-pass compilation. However, that does not generalize to the case where the grammar itself may change.

For additive grammar extensions, the pure bottom-up parse is well suited since it is a natural multi-pass parse. However, consider also the pathological input which includes code in extensible language Foo, but at the end retroactively obliterates all syntax rules for Foo (except perhaps the syntax needed to obliterate the rest of the language). Even the bottom-up parser would have trouble with this without discarding any state resulting from having applied the obliterated rules.

###Context hierarchies
Some parse rules may alter the parse context for any other rules contributing to their production. One example is an indent block in languages like Python or Yaml. Normally, these are handled by the lexer prior to grammatical parsing ([off-side rule](https://en.wikipedia.org/wiki/Off-side_rule)). However, since to maintain maximum flexibility Substrate does not include a tokenization phase, such rules are more easily handled through a parse context stack.

Pure bottom-up parsing does not handle hierarchical context mutations well, since the natural bottom-up parse may well apply parse rules to deeper scopes before recognizing their parent scopes. Special handling would be required to discard states resulting from such rule applications once the context was modified, which naively results in an unacceptable amount of wasted effort.

###Fault tolerance
The goal of a fault-tolerant parser is to offer one or more possible sets of transformations that could fix invalid syntax. This can be approximated in a top-down parser by relatively simple analyses on mismatches between the symbol sequence and pending matches.

Any backtracking parser can approximate this by allowing the assumption of errors at any point in the symbol sequence. However, as there are three types of principal symbol errors that may be assumed (found not expected, expected not found, and mismatch), this combinatorically grows the search space as more errors need to be assumed. A common way to reduce the search space involves "beacons" that, once produced, cannot be backtracked beyond.

##Design
Rules…
* may or may not be retroactive. Retroactive rules reset the scanner to the beginning after production.
* may alter the parse context before and after their application.
* may rewrite the scanner subsequence they consume.
* may not apply fully or at all at first. Wherever they get stuck, they are pushed onto a set of pending parses, to be resumed when the failure symbol is changed.
 * Parsing could resume from the furthest branch only, or from the first failure/on all branches, or from the beginning of the match.
 * This failure marking must take into consideration lookahead and lookbehind failures. Lookbehind failures that occur behind any pending rule production do not need to be considered for resumption.
