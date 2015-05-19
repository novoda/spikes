package com.novoda.landingstrip.setup;

public enum SmallData {

    A("A", "hello"),
    B("B", "world"),
    C("C", "foo"),
    D("D", "a big word");

    private final String title;
    private final String content;

    SmallData(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
