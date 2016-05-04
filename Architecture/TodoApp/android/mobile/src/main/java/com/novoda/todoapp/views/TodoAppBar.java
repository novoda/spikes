package com.novoda.todoapp.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

import com.novoda.notils.caster.Views;
import com.novoda.todoapp.R;

public class TodoAppBar extends AppBarLayout {

    @LayoutRes
    private int mergeLayoutId;
    @ColorRes
    private int textColorRes;

    public TodoAppBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        obtainCustomAttributes(context, attrs);
    }

    private void obtainCustomAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TodoAppBar);
        mergeLayoutId = typedArray.getResourceId(R.styleable.TodoAppBar_mergeLayoutId, R.layout.merge_app_bar_default);
        textColorRes = typedArray.getResourceId(R.styleable.TodoAppBar_textColorRes, android.R.color.white);
        typedArray.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), mergeLayoutId, this);
        Toolbar toolbar = Views.findById(this, R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(getContext(), textColorRes));
    }

}
