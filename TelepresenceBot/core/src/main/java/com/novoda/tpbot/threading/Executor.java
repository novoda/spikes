package com.novoda.tpbot.threading;

public interface Executor {

    void execute(Action action);

    interface DelayedExecutor extends Executor {
        void executeAfterDelay(Action action, long delay);

        void destroyDelayedAction(Action action);
    }

    interface Action {
        void perform();
    }

}
