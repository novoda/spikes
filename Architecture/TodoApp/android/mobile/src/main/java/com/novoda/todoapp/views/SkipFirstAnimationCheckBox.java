package com.novoda.todoapp.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;

//TODO improve animation issues when setting on resume.
public class SkipFirstAnimationCheckBox extends CheckBox {

    private boolean isFirstAnimation = true;

    public SkipFirstAnimationCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        if (isFirstAnimation) {
            isFirstAnimation = false;
            jumpDrawablesToCurrentState();
        }
    }

}
