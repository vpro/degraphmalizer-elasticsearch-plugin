package dgm.trees;

import java.util.HashSet;
import java.util.Set;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

/**
 *
 * @author wires
 */
public class NodeAlreadyVisitedTracker implements OccurrenceTracker<Pair<Edge, Vertex>> {
    final Set<Vertex> visited = new HashSet<Vertex>();

    @Override
    public boolean hasOccurred(Pair<Edge, Vertex> element) {
        return ! visited.add(element.b);
    }
}
