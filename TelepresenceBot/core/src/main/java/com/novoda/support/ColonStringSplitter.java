package com.novoda.support;

public class ColonStringSplitter {

    private static final String COLON = ":";

    public String[] split(String toSplit) {
        if (toSplit == null) {
            return new String[]{};
        } else {
            return toSplit.split(COLON);
        }
    }

}
