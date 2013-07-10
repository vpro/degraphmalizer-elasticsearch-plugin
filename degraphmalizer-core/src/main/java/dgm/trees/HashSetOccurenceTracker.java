package dgm.trees;

import java.util.HashSet;
import java.util.Set;

/**
 * @author  wires
 */
class HashSetOccurenceTracker<A> implements OccurrenceTracker<A> {
    final Set<A> contents = new HashSet<A>();

    @Override
    public boolean hasOccurred(A element) {
        return ! contents.add(element);
    }
}
