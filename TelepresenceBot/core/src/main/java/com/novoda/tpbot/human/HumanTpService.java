package com.novoda.tpbot.human;

import com.novoda.tpbot.Direction;
import com.novoda.tpbot.Result;

interface HumanTpService {

    Result connect();

    void moveIn(Direction direction);

    void disconnect();

}
