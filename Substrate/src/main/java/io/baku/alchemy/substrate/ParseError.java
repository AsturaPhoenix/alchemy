package io.baku.alchemy.substrate;

import io.baku.alchemy.substrate.predicates.ParsePredicate;
import io.baku.alchemy.substrate.symbols.Symbol;
import lombok.Value;

@Value
public class ParseError {
	Symbol found;
	ParsePredicate expected;
	
	@Override
	public String toString() {
		if (found == null) {
			return "Expected " + expected;
		} else if (expected == null) {
			return "Unexpected " + found.toDiagnosticString();
		} else {
			return "Expected " + expected + "; found " + found.toDiagnosticString();
		}
	}
}
