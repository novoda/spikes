package com.novoda.bonfire.navigation;

import com.novoda.bonfire.channel.data.model.Channel;

public interface Navigator {

    void toChannel(Channel channel);

    void toChannels();

    void toNewChannel();

    void toAddUsersFor(Channel channel);
}
