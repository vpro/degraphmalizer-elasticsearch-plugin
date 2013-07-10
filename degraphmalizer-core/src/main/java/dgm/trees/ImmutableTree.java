package dgm.trees;

import java.util.*;

public class ImmutableTree<A> implements Tree<A> {
    protected final TreeEntry<A> value;
    protected final Iterable<Tree<A>> children;

    @Override
    public A value() {
        return value.getValue();
    }
    @Override
    public TreeEntry<A> entry() {
        return value;
    }

    @Override
    public Iterable<Tree<A>> children() {
        return children;
    }

    public ImmutableTree(TreeEntry<A> value, Tree<A>... children) {
        this(value, Arrays.asList(children));
    }

    public ImmutableTree(final TreeEntry<A> value, final Iterable<Tree<A>> children) {
        this.value = value;
        this.children = children;
    }
}
