package com.novoda.simonsaysandroidthings.game;

import android.os.Handler;
import android.os.Message;

import com.novoda.simonsaysandroidthings.hw.io.Group;

import static com.novoda.simonsaysandroidthings.game.Sequencer.MSG_PROCESS_COMMAND;
import static com.novoda.simonsaysandroidthings.game.Sequencer.MSG_SEQUENCE_FINISHED;

abstract class SequencerCommand {

    private final Group group;
    private final int durationMs;

    private SequencerCommand nextCommand;

    private SequencerCommand(Group group, int durationMs) {
        this.group = group;
        this.durationMs = durationMs;
    }

    Group getGroup() {
        return group;
    }

    int getDurationMs() {
        return durationMs;
    }

    void setNextCommand(SequencerCommand nextCommand) {
        this.nextCommand = nextCommand;
    }

    SequencerCommand getNextCommand() {
        return nextCommand;
    }

    abstract void execute(Handler handler);

    final static class StartCommand extends SequencerCommand {

        StartCommand(Group group, int durationMs) {
            super(group, durationMs);
        }

        @Override
        public void execute(Handler handler) {
            getGroup().play();
            Message message = handler.obtainMessage(MSG_PROCESS_COMMAND, getNextCommand());
            handler.sendMessageDelayed(message, getDurationMs());
        }
    }

    final static class StopCommand extends SequencerCommand {

        StopCommand(Group group, int durationMs) {
            super(group, durationMs);
        }

        @Override
        public void execute(Handler handler) {
            getGroup().stop();
            Message message = handler.obtainMessage(MSG_PROCESS_COMMAND, getNextCommand());
            handler.sendMessageDelayed(message, getDurationMs());
        }
    }

    final static class FinishCommand extends SequencerCommand {

        FinishCommand(int delayMs) {
            super(null, delayMs);
        }

        @Override
        public void execute(Handler handler) {
            handler.sendEmptyMessageDelayed(MSG_SEQUENCE_FINISHED, getDurationMs());
        }
    }
}
