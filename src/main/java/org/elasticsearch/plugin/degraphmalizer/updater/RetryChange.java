package org.elasticsearch.plugin.degraphmalizer.updater;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
public class RetryChange extends Change {

    private int retries;
    private final String indexNameOrAlias;

    RetryChange(Change change, String indexNameOrAlias) {
        super(change.action(), change.type(), change.id(), change.version());
        retries = 0;
        this.indexNameOrAlias = indexNameOrAlias;
    }

    @Override
    public String getIndexNameOrAlias() {
        return this.indexNameOrAlias;
    }

    @Override
    public int retries() {
        return retries;
    }

    @Override
    public RetryChange retried(String name) {
        retries++;
        return this;
    }

    @Override
    public String toString() {
        return super.toString() + ", retries=" + retries;
    }


}
