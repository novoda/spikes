package com.novoda.frankboylan.meetingseating.seats;

import java.util.List;

interface SeatPresenter {
    void bind(SeatDisplayer displayer);

    void unbind();

    void onRefresh();

    void fillFilterView();

    void onApplyFilter(List<Seat> seatList);

    void startPresenting();
}
