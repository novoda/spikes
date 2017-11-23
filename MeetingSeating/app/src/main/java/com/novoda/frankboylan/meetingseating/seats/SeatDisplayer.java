package com.novoda.frankboylan.meetingseating.seats;

import com.google.firebase.database.DataSnapshot;
import com.novoda.frankboylan.meetingseating.rooms.Room;

import java.util.List;

interface SeatDisplayer {
    void addRoomSwitchElement(Room room);

    void addSeatSwitchElement(Seat seat);

    void updateSeatList(List<Seat> seatList);

    void updateSwitchList(List<Seat> cachedSeatList);

    void updateGreeting(DataSnapshot dataSnapshot);

    void resetAllSwitch();
}
