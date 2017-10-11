package com.novoda.tpbot;

public interface Executor {

    void execute(Action action);

    interface Action {
        void perform();
    }

}
