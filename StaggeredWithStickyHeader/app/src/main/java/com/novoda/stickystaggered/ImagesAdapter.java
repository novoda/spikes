package com.novoda.stickystaggered;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class ImagesAdapter extends ArrayAdapter<Integer> {
    private static final Integer[] COLORS = {
            Color.BLUE,
            Color.BLACK,
            Color.YELLOW,
            Color.CYAN,
            Color.MAGENTA,
            Color.BLUE,
            Color.DKGRAY,
            Color.GREEN,
            Color.GRAY,
            Color.WHITE,
            Color.LTGRAY,
            Color.BLUE,
            Color.BLACK,
            Color.YELLOW,
            Color.CYAN,
            Color.MAGENTA,
            Color.BLUE,
            Color.DKGRAY,
    };

    public ImagesAdapter(Context context) {
        super(context, 0, COLORS);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new View(getContext());
            convertView.setLayoutParams(new ViewGroup.LayoutParams(300, 300));
        }
        convertView.setBackgroundColor(COLORS[position]);
        return convertView;
    }
}
