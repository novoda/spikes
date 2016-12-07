package com.novoda.tpbot.human;

import com.novoda.tpbot.Direction;
import com.novoda.tpbot.Result;
import com.novoda.tpbot.support.Observable;

interface HumanTpService {

    Observable<Result> connect();

    void moveIn(Direction direction);

    void disconnect();

}
