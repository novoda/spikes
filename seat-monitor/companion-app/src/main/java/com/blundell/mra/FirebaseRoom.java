package com.blundell.mra;

import java.util.List;

class FirebaseRoom {
    private String location;

    private String roomId;
    private List<FirebaseSeatHeat> seatHeats;
    private FirebaseRoom() {
    }

    public FirebaseRoom(String location, String roomId, List<FirebaseSeatHeat> seatHeats) {
        this.location = location;
        this.roomId = roomId;
        this.seatHeats = seatHeats;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getLocation() {
        return location;
    }

    public List<FirebaseSeatHeat> getSeatHeats() {
        return seatHeats;
    }

    @Override
    public String toString() {
        return "FirebaseRoom{" +
                "roomId='" + roomId + '\'' +
                ", seatHeats=" + seatHeats +
                '}';
    }

}
