package com.novoda.tpbot.bot;

import com.novoda.tpbot.Direction;
import com.novoda.tpbot.Result;

interface BotTpService {

    Result connect();

    Direction listen();

    void disconnect();

}
