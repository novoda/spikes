package com.novoda.stickyheaderstest;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

public class ImagesAdapter extends BaseAdapter {

    private static final Integer HEADER_ITEM = -1;
    private static final Integer[] COLORS = {
            HEADER_ITEM, HEADER_ITEM,
            Color.BLUE,
            Color.BLACK,
            Color.YELLOW,
            Color.CYAN,
            Color.MAGENTA,
            Color.BLUE,
            Color.DKGRAY,
            Color.GREEN,
            Color.GRAY,
            Color.BLUE,
            Color.LTGRAY,
            Color.MAGENTA,
            Color.BLACK,
            Color.YELLOW,
            Color.CYAN,
            Color.MAGENTA,
            Color.BLUE,
            Color.DKGRAY,
    };
    private static final int HEADER_TYPE = 0;
    private static final int DEFAULT_TYPE = 1;
    private static final int VIEW_TYPE_COUNT = 2;

    private final Context context;

    public ImagesAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return COLORS.length;
    }

    @Override
    public Integer getItem(int position) {
        return COLORS[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) == DEFAULT_TYPE;
    }

    @Override
    public int getItemViewType(int position) {
        return COLORS[position] == HEADER_ITEM ? HEADER_TYPE : DEFAULT_TYPE;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItemViewType(position) == HEADER_TYPE) {
            convertView = new View(context);
            convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 500));
            convertView.setBackgroundColor(Color.WHITE);
            return convertView;
        }

        if (convertView == null) {
            convertView = new View(context);
            convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 300));
        }
        Integer item = COLORS[position];
        convertView.setBackgroundColor(item);
        return convertView;
    }
}
