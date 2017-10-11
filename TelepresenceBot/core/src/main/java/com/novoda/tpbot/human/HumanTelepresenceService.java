package com.novoda.tpbot.human;

import com.novoda.tpbot.model.Direction;
import com.novoda.tpbot.model.Result;
import com.novoda.tpbot.support.Observable;

interface HumanTelepresenceService {

    Observable<Result> connectTo(String serverAddress);

    void moveIn(Direction direction);

    void disconnect();

}
