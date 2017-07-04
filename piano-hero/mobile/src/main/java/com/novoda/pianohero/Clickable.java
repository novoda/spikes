package com.novoda.pianohero;

public interface Clickable {

    void setListener(Listener listener);

    interface Listener {

        void onClick();

    }

}
