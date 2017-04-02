package com.novoda.pianohero;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

class SimpleNoteConverter {

    private static final Map<String, Note> MAP = new HashMap<>();

    static {
        MAP.put("c", Note.C4);
        MAP.put("d", Note.D4);
        MAP.put("e", Note.E4);
        MAP.put("f", Note.F4);
        MAP.put("g", Note.G4);
        MAP.put("a", Note.A4);
        MAP.put("b", Note.B4);
    }

    public Note convert(String raw) {
        Note note = MAP.get(raw.toLowerCase(Locale.US));
        if (note == null) {
            throw new IllegalArgumentException("Argument doesn't correspond to simple note: " + raw);
        } else {
            return note;
        }
    }
}
