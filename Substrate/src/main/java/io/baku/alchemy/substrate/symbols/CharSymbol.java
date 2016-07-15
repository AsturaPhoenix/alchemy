package io.baku.alchemy.substrate.symbols;

import java.util.List;
import java.util.stream.Collectors;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class CharSymbol extends Symbol {
    public static final String TYPE = "character";
    
    public static List<CharSymbol> fromString(final String str) {
        return str.codePoints()
                .mapToObj(CharSymbol::new)
                .collect(Collectors.toList());
    }
    
    public CharSymbol(final int codePoint) {
        super(TYPE, codePoint);
    }
    
    public int getCodePoint() {
        return (int)getValue();
    }
    
    @Override
    public String toString() {
    	final int codePoint = getCodePoint();
    	switch (codePoint) {
    	case '"':
    		return "\\\"";
    	case '\\':
    		return "\\\\";
    	case '\b':
    		return "\\b";
    	case '\f':
    		return "\\f";
    	case '\n':
    		return "\\n";
    	case '\r':
    		return "\\r";
    	case '\t':
    		return "\\t";
    	default:
    		if (codePoint > 0xFFFF) {
    			return String.format("\\u%04x\\u%04x", codePoint >> 16, codePoint & 0xFFFF);
    		} else if (codePoint == ' ') {
    			return " ";
    		} else if (Character.isISOControl(codePoint) || Character.isWhitespace(codePoint)) {
    			return String.format("\\u%04x", codePoint);
    		} else {
    			return new String(Character.toChars(getCodePoint()));
    		}
    	}
    }
    
    @Override
    public String toDiagnosticString() {
    	final int codePoint = getCodePoint();
    	switch (codePoint) {
    	case '\'':
    		return "'";
    	case '\n':
    	case '\r':
    		return "newline";
    	case '\t':
    		return "tab";
    	default:
    		if (codePoint == ' ') {
    			return "' '";
    		} else if (Character.isWhitespace(codePoint)) {
    			return "whitespace";
    		} else if (Character.isISOControl(codePoint)) {
    			return "control character";
    		} else {
    			return "'" + new String(Character.toChars(getCodePoint())) + "'";
    		}
    	}
    }
}
