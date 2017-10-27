package com.blundell.mra;

public class FirebaseSeatHeat {
    private String seatId;
    private int heat;

    public FirebaseSeatHeat() {
    }

    FirebaseSeatHeat(String seatId, int heat) {
        this.seatId = seatId;
        this.heat = heat;
    }

    public String getSeatId() {
        return seatId;
    }

    public int getHeat() {
        return heat;
    }

    @Override
    public String toString() {
        return "FirebaseSeatHeat{" +
                "seatId='" + seatId + '\'' +
                ", heat=" + heat +
                '}';
    }
}
