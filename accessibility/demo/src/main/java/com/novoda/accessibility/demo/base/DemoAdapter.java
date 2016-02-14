package com.novoda.accessibility.demo.base;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.novoda.accessibility.demo.R;

import java.util.List;

public class DemoAdapter extends BaseAdapter {

    private final List<Demo> demos;

    public DemoAdapter(List<Demo> demos) {
        this.demos = demos;
    }

    @Override
    public int getCount() {
        return demos.size();
    }

    @Override
    public Demo getItem(int position) {
        return demos.get(position);
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
    public View getView(int position, View convertView, final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflateNewItemViewWithParent(parent);
        }
        Demo demo = getItem(position);
        bindView(((TextView) view), demo, parent.getContext());
        return view;
    }

    private void bindView(TextView view, final Demo demo, final Context context) {
        view.setText(demo.label);
        view.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, demo.activityClass);
                        context.startActivity(intent);
                    }

                }
        );
    }

    private View inflateNewItemViewWithParent(ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return layoutInflater.inflate(R.layout.view_demos_item, parent, false);
    }

}
