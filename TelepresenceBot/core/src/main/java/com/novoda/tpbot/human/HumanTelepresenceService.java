package com.novoda.tpbot.human;

import com.novoda.tpbot.Direction;
import com.novoda.tpbot.Result;
import com.novoda.support.Observable;

interface HumanTelepresenceService {

    Observable<Result> connectTo(String serverAddress);

    void moveIn(Direction direction);

    void disconnect();

}
