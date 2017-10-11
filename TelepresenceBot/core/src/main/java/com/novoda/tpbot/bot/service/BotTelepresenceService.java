package com.novoda.tpbot.bot.service;

import com.novoda.tpbot.support.Observable;
import com.novoda.tpbot.Direction;
import com.novoda.tpbot.Result;

public interface BotTelepresenceService {

    Observable<Result> connectTo(String serverAddress);

    Observable<Direction> listen();

    void disconnect();

}
