package com.novoda.bonfire.channel.data.model;

import java.io.Serializable;

public class Channel implements Serializable {

    private final String path;
    private final ChannelInfo channelInfo;

    public Channel(String path, ChannelInfo channelInfo) {
        this.path = path;
        this.channelInfo = channelInfo;
    }

    public ChannelInfo getChannelInfo() {
        return channelInfo;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return channelInfo.getName();
    }

    public boolean isPublic() {
        return channelInfo.getAccess().equalsIgnoreCase("public");
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

        return path != null ? path.equals(channel.path) : channel.path == null
                && channelInfo != null ? channelInfo.equals(channel.channelInfo) : channel.channelInfo == null;
    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (channelInfo != null ? channelInfo.hashCode() : 0);
        return result;
    }
}
