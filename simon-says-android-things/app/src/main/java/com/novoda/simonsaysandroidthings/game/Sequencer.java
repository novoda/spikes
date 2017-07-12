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

    private final SequencerHandler handler;

    Sequencer() {
        handler = new SequencerHandler();
    }

    void playRoundSequence(List<Group> sequence, int groupOnMs, int groupOffMs, OnSequenceFinishedListener listener) {
        List<SequencerCommand> commands = buildRoundSequenceCommands(sequence, groupOnMs, groupOffMs);
        enqueueSequenceCommands(commands, listener);
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
        FinishCommand finishCommand = createFinishCommand(lastCommand, 0);
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

    private StopCommand createStopCommand(Group group, int groupOffMs, SequencerCommand lastCommand) {
        StopCommand stopCommand = new StopCommand(group, groupOffMs);
        setNextCommandForLast(lastCommand, stopCommand);
        return stopCommand;
    }

    private FinishCommand createFinishCommand(SequencerCommand lastCommand, int delayMs) {
        FinishCommand finishCommand = new FinishCommand(delayMs);
        setNextCommandForLast(lastCommand, finishCommand);
        return finishCommand;
    }

    private void enqueueSequenceCommands(List<SequencerCommand> commands, OnSequenceFinishedListener listener) {
        handler.setListener(listener);
        Message message = handler.obtainMessage(MSG_PROCESS_COMMAND, commands.get(0));
        handler.sendMessage(message);
    }

    void playSuccessSequence(List<Group> groups, OnSequenceFinishedListener listener) {
        List<SequencerCommand> commands = new ArrayList<>();
        int frequency = 500;
        int groupsSize = groups.size();
        SequencerCommand lastCommand = null;
        for (int i = 0; i < groupsSize; i++) {
            Group group = Group.copyWithFrequency(groups.get(i), frequency);
            StartCommand startCommand = createStartCommand(group, 100, lastCommand);
            commands.add(startCommand);
            StopCommand stopCommand = createStopCommand(group, 0, startCommand);
            commands.add(stopCommand);
            lastCommand = stopCommand;
            frequency *= 2;
        }
        FinishCommand finishCommand = createFinishCommand(lastCommand, 1000);
        commands.add(finishCommand);
        enqueueSequenceCommands(commands, listener);
    }

    void playFailureSequence(List<Group> groups, OnSequenceFinishedListener listener) {
        List<SequencerCommand> commands = new ArrayList<>();
        int frequency = 200;
        int groupsSize = groups.size();
        SequencerCommand lastCommand = null;
        for (int i = groupsSize - 1; i >= 0; i--) {
            Group group = Group.copyWithFrequency(groups.get(i), frequency);
            StartCommand startCommand = createStartCommand(group, 100, lastCommand);
            commands.add(startCommand);
            StopCommand stopCommand = createStopCommand(group, 0, startCommand);
            commands.add(stopCommand);
            lastCommand = stopCommand;
            frequency /= 2;
        }
        FinishCommand finishCommand = createFinishCommand(lastCommand, 300);
        commands.add(finishCommand);
        enqueueSequenceCommands(commands, listener);
    }

    void playHighscoreSequence(List<Group> groups, OnSequenceFinishedListener listener) {
        List<SequencerCommand> commands = new ArrayList<>();
        int frequency = 1000;
        int groupsSize = groups.size();
        SequencerCommand lastCommand = null;
        for (int j = 0; j < 2; j++) {
            for (int i = 0; i <= (groupsSize - 1) / 2; i++) {
                Group group1 = Group.copyWithFrequency(groups.get(i), 1);
                Group group2 = Group.copyWithFrequency(groups.get(groupsSize - i - 1), frequency * (i + 1));

                StartCommand startCommand1 = createStartCommand(group1, 1, lastCommand);
                commands.add(startCommand1);
                StartCommand startCommand2 = createStartCommand(group2, 250, startCommand1);
                commands.add(startCommand2);

                StopCommand stopCommand1 = createStopCommand(group1, 0, startCommand2);
                StopCommand stopCommand2 = createStopCommand(group2, 0, stopCommand1);
                commands.add(stopCommand1);
                commands.add(stopCommand2);

                lastCommand = stopCommand2;
            }
        }

        FinishCommand finishCommand = createFinishCommand(lastCommand, 1000);
        commands.add(finishCommand);
        enqueueSequenceCommands(commands, listener);
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

        void setListener(OnSequenceFinishedListener listener) {
            this.listener = listener;
        }
    }

}
