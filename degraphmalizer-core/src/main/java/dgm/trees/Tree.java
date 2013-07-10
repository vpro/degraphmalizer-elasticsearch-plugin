package dgm.trees;

/**
 * A tree
 * <p/>
 * Each node stores a value and a list of child trees.
 * <p/>
 * <pre>
 *           .--(value)-->[]
 *          /
 * (value)-+----(value)-->[]   .--(value)-->[]
 *          \                 /
 *           `--(value)------+----(value)-->[]
 *
 * </pre>
 *
 * @param <A>
 * @author wires
 */
public interface Tree<A> {
    A value();

    TreeEntry<A> entry();

    Iterable<Tree<A>> children();
}
