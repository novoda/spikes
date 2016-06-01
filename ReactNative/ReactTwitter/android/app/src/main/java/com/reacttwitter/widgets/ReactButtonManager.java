package com.reacttwitter.widgets;

import android.graphics.Color;
import android.util.TypedValue;

import com.facebook.react.uimanager.BaseViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

class ReactButtonManager extends BaseViewManager<ReactButton, ReactButtonShadowNode> {

    private static final String REACT_CLASS = "RCTNovodaButton";

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

    @ReactProp(name = "textSize", defaultInt = 14)
    public void setTextSize(ReactButton reactButton, int textSize) {
        reactButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }

    @ReactProp(name = "backgroundImage")
    public void setBackgroundImage(ReactButton reactButton, String backgroundImage) {
        reactButton.setBackgroundImage(backgroundImage);
    }

    @Override
    public ReactButtonShadowNode createShadowNodeInstance() {
        return new ReactButtonShadowNode();
    }

    @Override
    public Class<ReactButtonShadowNode> getShadowNodeClass() {
        return ReactButtonShadowNode.class;
    }

    @Override
    public void updateExtraData(ReactButton root, Object extraData) {
        // noop
    }
}
