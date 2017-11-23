package com.novoda.frankboylan.meetingseating.rooms.heatmap.adv.repo;

public interface AdvHeatmapPresenter {
    void bind(AdvHeatmapDisplayer displayer);

    void unbind();

    void startPresenting();
}
