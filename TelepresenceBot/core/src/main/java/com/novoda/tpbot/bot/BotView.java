package com.novoda.tpbot.bot;

import com.novoda.tpbot.Direction;

interface BotView {

    void onConnect(String message);

    void onError(String message);

    void moveIn(Direction direction);

}
