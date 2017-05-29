package com.novoda.pianohero;

import java.util.HashMap;
import java.util.Map;

final class C4ToB5TrebleStaffPositioner {

    private final Map<Integer, Double> positions;

    static C4ToB5TrebleStaffPositioner createPositionerGivenNoteHeight(int noteHeight) {
        Map<Integer, Double> positions = new HashMap<>();
        positions.put(83, 0d);
        positions.put(82, 0.5 * noteHeight);
        positions.put(81, 0.5 * noteHeight);
        positions.put(80, noteHeight   * 1d);
        positions.put(79, noteHeight * 1d);
        positions.put(78, 1.5 * noteHeight);
        positions.put(77, 1.5 * noteHeight);
        positions.put(76, 2d * noteHeight);
        positions.put(75, 2.5 * noteHeight);
        positions.put(74, 2.5 * noteHeight);
        positions.put(73, 3d * noteHeight);
        positions.put(72, 3d * noteHeight);
        positions.put(71, 3.5 * noteHeight);
        positions.put(70, 4d * noteHeight);
        positions.put(69, 4d * noteHeight);
        positions.put(68, 4.5 * noteHeight);
        positions.put(67, 4.5 * noteHeight);
        positions.put(66, 5d * noteHeight);
        positions.put(65, 5d * noteHeight);
        positions.put(64, 5.5 * noteHeight);
        positions.put(63, 6d * noteHeight);
        positions.put(62, 6d * noteHeight);
        positions.put(61, 6.5 * noteHeight);
        positions.put(60, 6.5 * noteHeight);
        return new C4ToB5TrebleStaffPositioner(positions);
    }

    private C4ToB5TrebleStaffPositioner(Map<Integer, Double> positions) {
        this.positions = positions;
    }

    public double yPosition(Note note) {
        if (positions.containsKey(note.midi())) {
            return positions.get(note.midi());
        } else {
            throw new IllegalArgumentException("note out of bounds: " + note);
        }
    }

}
