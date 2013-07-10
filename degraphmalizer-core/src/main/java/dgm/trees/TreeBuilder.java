package dgm.trees;

import java.util.*;

/**
 * Build a tree by visiting it
 */
public class TreeBuilder<A> implements TreeVisitor<A> {
    final Deque<List<Tree<A>>> trees = new LinkedList<List<Tree<A>>>();
    Tree<A> root = null;
    int level = 0;

    @Override
    public boolean visitNode(A node, TreeViewer<A> viewer) {
        return false;
    }

    @Override
    public void beginChildren(A node, TreeViewer<A> viewer) {
        trees.addFirst(new ArrayList<Tree<A>>());
        level++;
    }

    @Override
    public void endChildren(A node, TreeViewer<A> viewer) {
        level--;
        final List<Tree<A>> children = trees.removeFirst();

        final List<Tree<A>> parent = trees.peekFirst();


        final Tree<A> tree = new ImmutableTree<A>(new TreeEntry<A>(node, level), children);

        // we have not reached the top of the queue
        if (parent != null) {
            parent.add(tree);
            return;
        }

        // we finished visiting, store the root
        root = tree;
    }

    public Tree<A> tree() {
        return root;
    }
}
