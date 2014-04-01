package org.elasticsearch.plugin.degraphmalizer.updater;

public class Change implements StringSerialization<Change> {
    private final Action action;
    private final String type;
    private final String id;
    private final long version;



    protected Change() {
        this(Action.UPDATE,"","",0);
    }

    protected Change(final Action action, final String type, final String id, long version) {
        this.action = action;
        this.type = type;
        this.id = id;
        this.version = version;
    }



    public Action action() {
        return action;
    }

    public String type() {
        return type;
    }

    public String id() {
        return id;
    }

    public long version() {
        return version;
    }

    public int retries() {
        return 0;
    }
    public RetryChange retried(String name) {
        return new RetryChange(this, name);
    }

    public String getIndexNameOrAlias() {
        return null;
    }


        @Override
	public String toValue() {
        return this.action().name() + "," + this.type() + "," + this.version() + "," + this.id();
    }

    @Override
	public Change fromValue(String value) {
        String[] values = value.split(",", 5);
        Action action = Action.valueOf(values[0]);
        String type = values[1];
        Long version = Long.valueOf(values[2]);
        String id = values[3];
        return new Change(action, type, id, version);
    }

    public static Change update(final String type, final String id, final long version) {
        return new Change(Action.UPDATE, type, id, version);
    }

    public static Change delete(final String type, final String id, final long version) {
        return new Change(Action.DELETE, type, id, version);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Change change = (Change) o;

        if (version != change.version) return false;
        if (action != change.action) return false;
        if (!id.equals(change.id)) return false;
        if (!type.equals(change.type)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = action.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + id.hashCode();
        result = 31 * result + (int) (version ^ (version >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Change{" +
                "action=" + action +
                ", type='" + type + '\'' +
                ", id='" + id + '\'' +
                ", version=" + version +
                "}";
    }
}
