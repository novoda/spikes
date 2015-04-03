package com.novoda.landingstrip;

public enum Data {

    A("A", "hello", android.R.drawable.ic_delete),
    B("B", "world", android.R.drawable.ic_input_add),
    C("C", "foo", android.R.drawable.ic_media_next),
    D("D", "bar", android.R.drawable.ic_media_pause),
    E("E", "bar", android.R.drawable.ic_media_pause),
    F("F", "bar", android.R.drawable.ic_media_pause);

    final String title;
    final String content;
    final int resId;

    Data(String title, String content, int resId) {
        this.title = title;
        this.content = content;
        this.resId = resId;
    }
}
