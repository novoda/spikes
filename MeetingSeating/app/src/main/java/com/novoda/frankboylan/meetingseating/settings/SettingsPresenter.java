package com.novoda.frankboylan.meetingseating.settings;

interface SettingsPresenter {
    void bind(SettingsDisplayer displayer);

    void unbind();

    void loadDataset(int i);
}
