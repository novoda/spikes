package com.reacttwitter.widgets;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

public class ReactButtonManager extends SimpleViewManager<ReactButton> {

    public static final String REACT_CLASS = "RCTNovodaButton";

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected ReactButton createViewInstance(ThemedReactContext reactContext) {
        return new ReactButton(reactContext);
    }

    @ReactProp(name = "enabled")
    public void setEnabled(ReactButton reactButton, boolean enabled) {
        reactButton.setEnabled(enabled);
    }

    @ReactProp(name = "text")
    public void setText(ReactButton reactButton, String text) {
        reactButton.setText(text);
    }

    @ReactProp(name = "textColor")
    public void setTextColor(ReactButton reactButton, String color) {
        reactButton.setTextColor(Color.parseColor(color));
    }
}
