package com.novoda.tpbot.bot.service;

import com.novoda.support.Observable;
import com.novoda.tpbot.Direction;
import com.novoda.tpbot.Result;

interface BotTelepresenceService {

    Observable<Result> connectTo(String serverAddress);

    Observable<Direction> listen();

    void disconnect();

}
