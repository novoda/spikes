package com.reacttwitter.widgets;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

public class ReactButtonManager extends SimpleViewManager<ReactButton> {

    public static final String REACT_CLASS = "RCTButton";

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected ReactButton createViewInstance(ThemedReactContext reactContext) {
        return new ReactButton(reactContext);
    }

    @ReactProp(name = "text")
    public void setText(ReactButton reactButton, String text) {
        reactButton.setText(text);
    }
}
