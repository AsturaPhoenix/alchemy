package io.baku.alchemy.substrate.symbols;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.collect.Iterables;

import edu.umd.cs.findbugs.annotations.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Symbol {
    private final String type;
    private final Object value;
    private final List<Symbol> children;
    
    public Symbol(final String type, final List<Symbol> children) {
        this(type, null, children);
    }
    
    public Symbol(final String type, final Object value, final Symbol... children) {
        this(type, value, Arrays.asList(children));
    }
    
    public Symbol(final Object value) {
        type = getClass().getSimpleName();
        this.value = value;
        children = Collections.emptyList();
    }
    
    @SuppressWarnings("unchecked")
    public <T> boolean testValue(final Class<T> type, final Predicate<T> refinement) {
        return type.isInstance(value)? refinement.test((T)value) : false;
    }
    
    @Override
    public String toString() {
        return getValue() == null? getType() : getType() + "(" + getValue() + ")";
    }
    
    public String toDiagnosticString() {
    	return getValue() == null? getType() : getValue().toString();
    }
    
    public String printTree() {
        return printTree(0);
    }
    
    public String printTree(final int depth) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            builder.append('-');
        }
        builder.append(toString());
        if (getValue() == null) {
            for (final Symbol child : children) {
                builder.append('\n').append(child.printTree(depth + 1));
            }
        }
        return builder.toString();
    }
    
    public static List<Symbol> filterType(final Collection<Symbol> symbols, final String type) {
    	return symbols.stream()
    			.filter(s -> type.equals(s.getType()))
    			.collect(Collectors.toList());
    }
    
    public static Symbol getByType(final Collection<Symbol> symbols, final String type) {
    	return Iterables.getOnlyElement(filterType(symbols, type));
    }
    
    public static Symbol getByType(final Collection<Symbol> symbols, final String type, final @Nullable Symbol defaultValue) {
    	return Iterables.getOnlyElement(filterType(symbols, type), defaultValue);
    }
    
    public static String stringValue(final Collection<Symbol> symbols) {
        return symbols.stream()
                .map(Object::toString)
                .collect(Collectors.joining());
    }
}
