package com.blundell.mra;

import java.util.List;

public class FirebaseOfficeHeatMaps {

    private String office;
    private List<FirebaseRoom> rooms;

    public FirebaseOfficeHeatMaps() {
        // For Firebase recreation
    }

    public FirebaseOfficeHeatMaps(String office, List<FirebaseRoom> rooms) {
        this.office = office;
        this.rooms = rooms;
    }

    public String getOffice() {
        return office;
    }

    public List<FirebaseRoom> getRooms() {
        return rooms;
    }

    @Override
    public String toString() {
        return "FirebaseOfficeHeatMaps{" +
                "rooms=" + rooms +
                '}';
    }
}
