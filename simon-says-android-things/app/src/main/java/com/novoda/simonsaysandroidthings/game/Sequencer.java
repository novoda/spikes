package com.novoda.simonsaysandroidthings.game;

import android.os.Handler;
import android.os.Message;

import com.novoda.simonsaysandroidthings.game.SequencerCommand.FinishCommand;
import com.novoda.simonsaysandroidthings.game.SequencerCommand.StartCommand;
import com.novoda.simonsaysandroidthings.game.SequencerCommand.StopCommand;
import com.novoda.simonsaysandroidthings.hw.io.Group;

import java.util.ArrayList;
import java.util.List;

class Sequencer {

    static final int MSG_PROCESS_COMMAND = 1;
    static final int MSG_SEQUENCE_FINISHED = 2;

    private final Handler handler;

    Sequencer(OnSequenceFinishedListener listener) {
        handler = new SequencerHandler(listener);
    }

    void playRoundSequence(List<Group> sequence, int groupOnMs, int groupOffMs) {
        List<SequencerCommand> commands = buildRoundSequenceCommands(sequence, groupOnMs, groupOffMs);
        enqueueSequenceCommands(commands);
    }

    private List<SequencerCommand> buildRoundSequenceCommands(List<Group> sequence, int groupOnMs, int groupOffMs) {
        List<SequencerCommand> commands = new ArrayList<>();
        int sequenceSize = sequence.size();
        SequencerCommand lastCommand = null;
        for (int i = 0; i < sequenceSize; i++) {
            StartCommand startCommand = createStartCommand(sequence.get(i), groupOnMs, lastCommand);
            commands.add(startCommand);
            StopCommand stopCommand = createStopCommand(sequence.get(i), groupOffMs, startCommand);
            commands.add(stopCommand);
            lastCommand = stopCommand;
        }
        FinishCommand finishCommand = createFinishCommand(lastCommand);
        commands.add(finishCommand);
        return commands;
    }

    private StartCommand createStartCommand(Group group, int groupOnMs, SequencerCommand lastCommand) {
        StartCommand startCommand = new StartCommand(group, groupOnMs);
        setNextCommandForLast(lastCommand, startCommand);
        return startCommand;
    }

    private void setNextCommandForLast(SequencerCommand lastCommand, SequencerCommand nextCommand) {
        if (lastCommand != null) {
            lastCommand.setNextCommand(nextCommand);
        }
    }

    private StopCommand createStopCommand(Group group, int groupOffMs, StartCommand lastCommand) {
        StopCommand stopCommand = new StopCommand(group, groupOffMs);
        setNextCommandForLast(lastCommand, stopCommand);
        return stopCommand;
    }

    private FinishCommand createFinishCommand(SequencerCommand lastCommand) {
        FinishCommand finishCommand = new FinishCommand();
        setNextCommandForLast(lastCommand, finishCommand);
        return finishCommand;
    }

    private void enqueueSequenceCommands(List<SequencerCommand> commands) {
        Message message = handler.obtainMessage(MSG_PROCESS_COMMAND, commands.get(0));
        handler.sendMessage(message);
    }

    void playSuccessSequence(List<Group> groups, OnSequenceFinishedListener listener) {

    }

    void stop() {
        handler.removeMessages(MSG_PROCESS_COMMAND);
        handler.removeMessages(MSG_SEQUENCE_FINISHED);
    }

    interface OnSequenceFinishedListener {

        void onSequenceFinished();

    }

    private static class SequencerHandler extends Handler {

        private OnSequenceFinishedListener listener;

        private SequencerHandler(OnSequenceFinishedListener listener) {
            this.listener = listener;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PROCESS_COMMAND:
                    SequencerCommand command = (SequencerCommand) msg.obj;
                    command.execute(this);
                    break;
                case MSG_SEQUENCE_FINISHED:
                    listener.onSequenceFinished();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }

    }

}
