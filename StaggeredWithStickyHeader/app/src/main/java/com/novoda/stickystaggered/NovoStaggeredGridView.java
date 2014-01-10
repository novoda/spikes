package com.novoda.stickystaggered;

import android.content.Context;
import android.util.AttributeSet;

import com.etsy.android.grid.StaggeredGridView;

public class NovoStaggeredGridView extends StaggeredGridView {
    public NovoStaggeredGridView(Context context) {
        super(context);
    }

    public NovoStaggeredGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NovoStaggeredGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOffsetY(int offset) {
        offsetChildrenTopAndBottom(offset);
    }
}
