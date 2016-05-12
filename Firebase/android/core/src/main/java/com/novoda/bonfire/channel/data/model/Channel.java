package com.novoda.bonfire.channel.data.model;

public class Channel {

    private String name;

    @SuppressWarnings("unused") //Used by Firebase
    public Channel() {
    }

    public Channel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
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

        return name != null ? name.equals(channel.name) : channel.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
