package com.novoda.todoapp.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.LayoutRes;
import android.support.design.widget.AppBarLayout;
import android.util.AttributeSet;
import android.view.View;

import com.novoda.todoapp.R;

public class TodoAppBar extends AppBarLayout {

    @LayoutRes
    private int mergeLayoutId;

    public TodoAppBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        obtainCustomAttributes(context, attrs);
    }

    private void obtainCustomAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TodoAppBar);
        mergeLayoutId = typedArray.getResourceId(R.styleable.TodoAppBar_mergeLayoutId, R.layout.merge_app_bar_default);
        typedArray.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), mergeLayoutId, this);
    }

}
