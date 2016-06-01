package com.novoda.bonfire.channel.data.model;

public class Channel {

    public enum Access {
        PRIVATE, PUBLIC
    }

    private final String name;
    private final Access access;

    public Channel(String name, Access access) {
        this.name = name;
        this.access = access;
    }

    public String getName() {
        return name;
    }

    public Access getAccess() {
        return access;
    }

    public boolean isPrivate() {
        return access == Access.PRIVATE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Channel channel = (Channel) o;

        return name != null ? name.equals(channel.name) : channel.name == null
                && access == channel.access;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (access != null ? access.hashCode() : 0);
        return result;
    }
}
