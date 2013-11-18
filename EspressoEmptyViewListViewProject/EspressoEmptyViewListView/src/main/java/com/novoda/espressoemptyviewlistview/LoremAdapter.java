package com.novoda.espressoemptyviewlistview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LoremAdapter extends BaseAdapter {

    private static final String[] ITEMS = {
            "Lorem ipsum dolor sit amet, consectetuer adipiscing elit",
            "Aliquam tincidunt mauris eu risus",
            "Vestibulum auctor dapibus neque",
            "Nunc dignissim risus id metus",
            "Cras ornare tristique elit",
            "Vivamus vestibulum nulla nec ante",
            "Praesent placerat risus quis eros",
            "Fusce pellentesque suscipit nibh",
            "Integer vitae libero ac risus egestas placerat",
            "Vestibulum commodo felis quis tortor",
            "Ut aliquam sollicitudin leo",
            "Cras iaculis ultricies nulla",
            "Donec quis dui at dolor tempor interdum",
            "Vivamus molestie gravida turpis",
            "Fusce lobortislorem at ipsum semper sagittis",
            "Nam convallis pellentesque nisl",
            "Integer malesuada commodo nulla"
    };
    public static final int TEXT_VIEW_PADDING = 20;
    private boolean isLoading = true;

    @Override
    public int getCount() {
        return isLoading ? 0 : ITEMS.length;
    }

    @Override
    public String getItem(int position) {
        return ITEMS[position];
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getTextView(position, parent.getContext());
    }

    private TextView getTextView(int position, Context context) {
        final TextView textView = new TextView(context);
        textView.setText(getItem(position));
        textView.setPadding(TEXT_VIEW_PADDING, TEXT_VIEW_PADDING, TEXT_VIEW_PADDING, TEXT_VIEW_PADDING);
        return textView;
    }

    public void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
        notifyDataSetChanged();
    }
}
