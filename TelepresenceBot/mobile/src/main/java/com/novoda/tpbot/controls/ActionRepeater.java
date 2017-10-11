package com.novoda.tpbot.controls;

import com.novoda.tpbot.Executor;

import java.util.concurrent.TimeUnit;

public class ActionRepeater {

    private static final long ACTION_REPEAT_DELAY = TimeUnit.MILLISECONDS.toMillis(80);

    private final Listener listener;
    private final Executor.DelayedExecutor delayedExecutor;
    private String currentCommand;

    public ActionRepeater(Listener listener, Executor.DelayedExecutor delayedExecutor) {
        this.listener = listener;
        this.delayedExecutor = delayedExecutor;
    }

    public void startRepeatingCommand(String command) {
        currentCommand = command;
        sendRepeatingCommand(currentCommand);
    }

    public void stopCurrentRepeatingCommand() {
        stopRepeatingCommand(currentCommand);
    }

    public void stopRepeatingCommand(String command) {
        if (currentCommand != null && currentCommand.equals(command)) {
            delayedExecutor.destroyDelayedAction(repeatAction);
            currentCommand = null;
        }
    }

    private final Executor.Action repeatAction = new Executor.Action() {
        @Override
        public void perform() {
            sendRepeatingCommand(ActionRepeater.this.currentCommand);
        }
    };

    private void sendRepeatingCommand(String command) {
        listener.onActionRepeated(command);
        delayedExecutor.executeAfterDelay(repeatAction, ACTION_REPEAT_DELAY);
    }

    public interface Listener {
        void onActionRepeated(String command);
    }
}
