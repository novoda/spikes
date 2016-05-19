package com.novoda.bonfire.channel.data.model;

import java.io.Serializable;

public class ChannelInfo implements Serializable {

    private String name;
    private String access;

    @SuppressWarnings("unused") // used by Firebase
    public ChannelInfo() {
    }

    public ChannelInfo(String name, boolean isPrivate) {
        this.name = name;
        this.access = isPrivate ? "private" : "public";
    }

    public String getName() {
        return name;
    }

    public String getAccess() {
        return access;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChannelInfo that = (ChannelInfo) o;

        return name != null ? name.equals(that.name) : that.name == null
                && access != null ? access.equals(that.access) : that.access == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (access != null ? access.hashCode() : 0);
        return result;
    }
}
