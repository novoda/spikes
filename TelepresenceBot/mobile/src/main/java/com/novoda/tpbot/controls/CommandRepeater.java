package com.novoda.tpbot.controls;

import android.os.Handler;

import java.util.concurrent.TimeUnit;

public class CommandRepeater {

    private static final long COMMAND_REPEAT_DELAY = TimeUnit.MILLISECONDS.toMillis(100);

    private final Listener listener;
    private final Handler handler;
    private String currentCommand;

    public CommandRepeater(Listener listener, Handler handler) {
        this.listener = listener;
        this.handler = handler;
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
            handler.removeCallbacks(repeatCommand);
            currentCommand = null;
        }
    }

    private final Runnable repeatCommand = new Runnable() {
        @Override
        public void run() {
            sendRepeatingCommand(CommandRepeater.this.currentCommand);
        }
    };

    private void sendRepeatingCommand(String command) {
        listener.onCommandRepeated(command);
        handler.postDelayed(repeatCommand, COMMAND_REPEAT_DELAY);
    }

    public interface Listener {
        void onCommandRepeated(String command);
    }
}
