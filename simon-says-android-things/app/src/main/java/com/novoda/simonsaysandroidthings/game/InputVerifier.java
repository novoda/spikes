package com.novoda.simonsaysandroidthings.game;

import com.novoda.simonsaysandroidthings.hw.io.Group;

import java.util.Collections;
import java.util.List;

class InputVerifier {

    private final List<Group> groups;

    private InputVerificationListener inputVerificationListener;
    private List<Group> sequenceToVerify;
    private int currentIndex;
    private boolean listening;

    InputVerifier(List<Group> groups) {
        this.groups = groups;
        init();
    }

    private void init() {
        for (Group group : groups) {
            group.setOnGroupButtonPressedListener(groupButtonPressedListener);
        }
    }

    void listenFor(List<Group> sequenceToVerify, InputVerificationListener inputVerificationListener) {
        this.sequenceToVerify = sequenceToVerify;
        this.inputVerificationListener = inputVerificationListener;
        listening = true;
    }

    private final Group.OnGroupButtonPressedListener groupButtonPressedListener = new Group.OnGroupButtonPressedListener() {
        @Override
        public void onGroupButtonPressed(Group group) {
            if (currentIndex < sequenceToVerify.size() && sequenceToVerify.get(currentIndex) != group) {
                inputVerificationListener.onWrongInput();
                stop();
                return;
            }
            currentIndex++;
            if (currentIndex == sequenceToVerify.size()) {
                inputVerificationListener.onCorrectInput();
                stop();
            }
        }

        @Override
        public boolean isEnabled() {
            return listening;
        }
    };

    void stop() {
        listening = false;
        currentIndex = 0;
        sequenceToVerify = Collections.emptyList();
        inputVerificationListener = null;
    }

    interface InputVerificationListener {

        void onCorrectInput();

        void onWrongInput();

    }

}
