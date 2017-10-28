package com.blundell.mra;

import java.util.HashMap;
import java.util.Map;

public class SeatGrid {

    private static Map<String, Position> lookupTable = new HashMap<>();

    static {
        lookupTable.put("lpl1", new Position("downstairs", "lpl1", 0, 2));
        lookupTable.put("lpl2", new Position("downstairs", "lpl1", 0, 3));
        lookupTable.put("lpl3", new Position("downstairs", "lpl1", 1, 5));
        lookupTable.put("lpl4", new Position("downstairs", "lpl1", 2, 5));

        lookupTable.put("ldn1", new Position("fishbowl", "ldn1", 2, 5));
        lookupTable.put("ldn2", new Position("fishbowl", "ldn2", 2, 5));
        lookupTable.put("ldn3", new Position("fishbowl", "ldn3", 2, 5));
        lookupTable.put("ldn4", new Position("fishbowl", "ldn1", 2, 5));

        lookupTable.put("ldn5", new Position("subcave", "ldn5", 2, 5));
        lookupTable.put("ldn6", new Position("subcave", "ldn6", 2, 5));
        lookupTable.put("ldn7", new Position("subcave", "ldn7", 2, 5));
        lookupTable.put("ldn8", new Position("subcave", "ldn8", 2, 5));
    }

    public static Position lookup(String seatId) {
        return lookupTable.get(seatId);
    }

    public static class Position {
        private final String location;
        private final String seatId;
        private final int x;
        private final int y;

        Position(String location, String seatId, int x, int y) {
            this.location = location;
            this.seatId = seatId;
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @Override
        public String toString() {
            return "Position{"
                    + "location='" + location + '\''
                    + ", seatId='" + seatId + '\''
                    + ", x=" + x
                    + ", y=" + y
                    + '}';
        }
    }

}
