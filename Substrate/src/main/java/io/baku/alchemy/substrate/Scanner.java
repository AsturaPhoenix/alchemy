package io.baku.alchemy.substrate;

import java.util.List;

import com.google.common.collect.Lists;

import lombok.RequiredArgsConstructor;
import lombok.Value;

public interface Scanner<T> {
    List<T> getSequence();
    int getCursor();
    int getCycleStart();
    
    default T getCurrent() {
        return getSequence().get(getCursor());
    }
    
    default boolean hasCurrent() {
        return getCursor() < getSequence().size();
    }
    
    /**
     * Advances the cursor by one, wrapping around at the end.
     */
    default Scanner<T> advance() {
        return advance(1, true);
    }
    
    default Scanner<T> advance(final int delta, final boolean wrapAround) {
        int next = getCursor() + delta;
        if (wrapAround) {
	        if (getSequence().isEmpty()) {
	        	next = 0;
	        } else {
	        	next %= getSequence().size();
	        }
        }
        return new Simple<>(getSequence(), next, getCycleStart());
    }
    
    /**
     * Creates a reversed view of this scanner, with the cursor adjusted to point to the previous
     * element. This is useful for evaluating lookbehind assertions.
     */
    default Scanner<T> reverse() {
        return new Simple<>(Lists.reverse(getSequence()), getSequence().size() - getCursor());
    }
    
    @Value
    @RequiredArgsConstructor
    public static class Simple<T> implements Scanner<T> {
        List<T> sequence;
        int cursor, cycleStart;
        
        public Simple(final List<T> sequence) {
            this(sequence, 0);
        }
        
        public Simple(final List<T> sequence, final int cursor) {
            this(sequence, cursor, cursor);
        }
        
        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            
            for (int i = 0; i < sequence.size(); i++) {
                if (i == cycleStart) {
                    builder.append('>');
                } else if (i == cursor) {
                    builder.append('*');
                } else if (i > 0) {
                    builder.append(' ');
                }
                
                builder.append(sequence.get(i));
            }
            
            if (sequence.size() == cursor) {
                builder.append('*');
            }
            
            return builder.toString();
        }
    }
}
