package com.novoda.tpbot.bot;

import com.novoda.tpbot.Direction;
import com.novoda.tpbot.Result;
import com.novoda.tpbot.support.Observable;

interface BotTpService {

    Observable<Result> connectTo(String serverAddress);

    Observable<Direction> listen();

    void disconnect();

}
