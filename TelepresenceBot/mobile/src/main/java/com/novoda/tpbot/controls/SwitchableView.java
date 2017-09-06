package com.novoda.tpbot.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ViewSwitcher;

import com.novoda.notils.exception.DeveloperError;
import com.novoda.tpbot.R;

public class SwitchableView extends ViewSwitcher {

    private static final int NOT_SET = -1;

    public enum View {
        SERVER_DECLARATION_VIEW(0),
        CONTROLLER_VIEW(1);

        private final int viewIndex;

        View(int viewIndex) {
            this.viewIndex = viewIndex;
        }
    }

    public SwitchableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomAttributes(context, attrs);
    }

    private void applyCustomAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwitchableView);

        if (typedArray == null) {
            return;
        }

        int baseLayoutId;
        int layoutToSwitchWithId;

        try {
            baseLayoutId = typedArray.getResourceId(R.styleable.SwitchableView_base_layout, NOT_SET);
            layoutToSwitchWithId = typedArray.getResourceId(R.styleable.SwitchableView_layout_to_switch_with, NOT_SET);

            if (baseLayoutId == NOT_SET || layoutToSwitchWithId == NOT_SET) {
                throw new DeveloperError("A base_layout and layout_to_switch_to MUST be defined.");
            }

        } finally {
            typedArray.recycle();
        }

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(baseLayoutId, this, true);
        layoutInflater.inflate(layoutToSwitchWithId, this, true);
    }

    public void switchTo(View view) {
        setDisplayedChild(view.viewIndex);
    }

}
