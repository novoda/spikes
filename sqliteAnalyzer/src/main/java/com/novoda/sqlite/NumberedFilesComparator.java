package com.novoda.sqlite;

import java.io.File;
import java.util.Comparator;

final class NumberedFilesComparator implements
        Comparator<File> {
    @Override
    public int compare(File o1, File o2) {
        return getLeadingNumber(o1.getName())
                - getLeadingNumber(o2.getName());
    }

    private int getLeadingNumber(String name) {
        try {
            return Integer.valueOf(name.split("_")[0]);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}