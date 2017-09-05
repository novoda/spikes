package com.novoda.tpbot.controls;

import android.os.Handler;

import java.util.concurrent.TimeUnit;

public class CommandRepeater {

    private static final long COMMAND_REPEAT_DELAY = TimeUnit.MILLISECONDS.toMillis(80);

    private final Handler handler;
    private CommandRepeatingRunnable repeater;

    public CommandRepeater(Handler handler) {
        this.handler = handler;
    }

    public void startRepeatingCommand(String command, Listener listener) {
        sendRepeatingCommand(command, listener);
    }

    public void stopCurrentRepeatingCommand() {
        stopRepeatingCommand();
    }

    public void stopRepeatingCommand() {
        if (repeater != null) {
            handler.removeCallbacks(repeater);
            repeater = null;
        }
    }

    private void sendRepeatingCommand(String command, Listener listener) {
        repeater = new CommandRepeatingRunnable(command, listener);
        listener.onCommandRepeated(command);
        handler.postDelayed(repeater, COMMAND_REPEAT_DELAY);
    }

    private class CommandRepeatingRunnable implements Runnable {

        private final String command;
        private final Listener listener;

        private CommandRepeatingRunnable(String command, Listener listener) {
            this.command = command;
            this.listener = listener;
        }

        @Override
        public void run() {
            listener.onCommandRepeated(command);
            handler.postDelayed(this, COMMAND_REPEAT_DELAY);
        }
    }

    public interface Listener {
        void onCommandRepeated(String command);
    }
}
