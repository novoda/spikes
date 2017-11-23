package com.novoda.frankboylan.meetingseating.seats;

public interface SeatEnabler {
    void uncheckSeatsWithMatchingId(int roomId);

    void checkSeatsWithMatchingId(int roomId);
}
