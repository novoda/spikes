package com.novoda.bonfire.channel.data.model;

import java.util.List;

public class Channels {

    private final List<Channel> channels;

    public Channels(List<Channel> channels) {
        this.channels = channels;
    }

    public Channel getChannelAt(int position) {
        return channels.get(position);
    }

    public int size() {
        return channels.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Channels channels1 = (Channels) o;

        return channels != null ? channels.equals(channels1.channels) : channels1.channels == null;
    }

    @Override
    public int hashCode() {
        return channels != null ? channels.hashCode() : 0;
    }
}
