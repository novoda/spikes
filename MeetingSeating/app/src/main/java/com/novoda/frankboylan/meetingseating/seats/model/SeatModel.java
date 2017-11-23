package com.novoda.frankboylan.meetingseating.seats.model;

import com.novoda.frankboylan.meetingseating.rooms.Room;
import com.novoda.frankboylan.meetingseating.seats.Seat;

import java.util.List;

public interface SeatModel {
    void retrieveData(SeatModelListener seatModelListener);

    List<Seat> getAllSeats();

    List<Room> getAllRooms();

    void addSeatToCache(Seat seat);

    void clearSeatCache();

    List<Seat> getCachedList();

    boolean isCacheActive();

    void setMetaCacheToActive();

    public interface SeatModelListener {
        void onSeatModelChanged(List<Seat> seatList);
    }
}
