package dgm.trees;

/**
 * @author Michiel Meeuwissen
 */
public class TreeEntry<E> {

    private final E value;
    private final Integer distance;

    public TreeEntry(E value, Integer distance) {
        this.value = value;
        this.distance = distance;
    }

    public E getValue() {
        return value;
    }

    public Integer getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return distance + ":" + getValue();
    }

}
