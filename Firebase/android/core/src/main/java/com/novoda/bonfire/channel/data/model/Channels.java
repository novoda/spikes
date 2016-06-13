package com.novoda.bonfire.channel.data.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    public Channels sortedByName() {
        List<Channel> sortedList = new ArrayList<>(channels);
        Collections.sort(sortedList, byName());
        return new Channels(sortedList);
    }

    private static Comparator<? super Channel> byName() {
        return new Comparator<Channel>() {
            @Override
            public int compare(Channel o1, Channel o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
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
