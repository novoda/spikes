package com.novoda.simonsaysandroidthings.game;

import android.os.Handler;
import android.os.Message;

import com.novoda.simonsaysandroidthings.hw.io.Group;

import java.util.ArrayList;
import java.util.List;

class Sequencer {

    private static final int MSG_PROCESS_COMMAND = 1;
    private static final int MSG_SEQUENCE_FINISHED = 2;

    private Handler handler = new SequencerHandler();
    private OnSequenceFinishedListener listener;

    void play(List<Group> sequence, int groupOnMs, int groupOffMs, OnSequenceFinishedListener listener) {
        this.listener = listener;
        List<Command> commands = buildCommands(sequence, groupOnMs, groupOffMs);
        enqueueCommands(commands);
    }

    private List<Command> buildCommands(List<Group> sequence, int groupOnMs, int groupOffMs) {
        List<Command> commands = new ArrayList<>();
        int size = sequence.size();
        for (int i = 0; i < size; i++) {
            commands.add(new Command(groupOnMs, Command.Type.START, sequence.get(i)));
            commands.add(new Command(groupOffMs, Command.Type.STOP, sequence.get(i)));
        }
        commands.add(new Command(1, Command.Type.FINISHED, null));
        return commands;
    }

    private void enqueueCommands(List<Command> commands) {
        Message message = handler.obtainMessage(MSG_PROCESS_COMMAND, 0, 0, commands);
        handler.sendMessage(message);
    }

    public void stop() {
        handler.removeMessages(MSG_PROCESS_COMMAND);
        handler.removeMessages(MSG_SEQUENCE_FINISHED);
    }

    interface OnSequenceFinishedListener {

        void onSequenceFinished();

    }

    private class SequencerHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PROCESS_COMMAND:
                    //noinspection unchecked
                    List<Command> commands = (List<Command>) msg.obj;
                    int current = msg.arg1;
                    commands.get(current).type.execute(this, current, commands);
                    break;
                case MSG_SEQUENCE_FINISHED:
                    if (listener != null) {
                        listener.onSequenceFinished();
                    }
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }

    }

    private static class Command {

        Command(int durationMs, Type type, Group group) {
            this.durationMs = durationMs;
            this.type = type;
            this.group = group;
        }

        enum Type {

            START {
                @Override
                void execute(Handler handler, int current, List<Command> commands) {
                    Command command = commands.get(current);
                    command.group.play();
                    Message message = handler.obtainMessage(MSG_PROCESS_COMMAND, current + 1, 0, commands);
                    handler.sendMessageDelayed(message, command.durationMs);
                }

            },
            STOP {
                @Override
                void execute(Handler handler, int current, List<Command> commands) {
                    Command command = commands.get(current);
                    command.group.stop();
                    Message message = handler.obtainMessage(MSG_PROCESS_COMMAND, current + 1, 0, commands);
                    handler.sendMessageDelayed(message, command.durationMs);
                }

            },
            FINISHED {
                @Override
                void execute(Handler handler, int current, List<Command> commands) {
                    handler.sendEmptyMessage(MSG_SEQUENCE_FINISHED);
                }

            };

            abstract void execute(Handler sequencerHandler, int current, List<Command> commands);

        }

        private final int durationMs;
        private final Type type;
        private final Group group;

    }

}
