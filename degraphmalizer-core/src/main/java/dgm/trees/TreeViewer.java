package dgm.trees;

/**
 * View objects of type <code>A</code> as a tree
 *
 * @author wires
 */
public interface TreeViewer<A> {
    /**
     * Get all children of <code>node</code>
     */
    Iterable<A> children(A node);
}
