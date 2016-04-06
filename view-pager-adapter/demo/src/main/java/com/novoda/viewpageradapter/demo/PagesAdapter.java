package com.novoda.viewpageradapter.demo;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.novoda.viewpageradapter.ViewPagerAdapter;

import java.util.List;

class PagesAdapter extends ViewPagerAdapter<RecyclerView> {

    private final ItemClickListener listener;
    private List<Page> pages;

    PagesAdapter(ItemClickListener listener, List<Page> pages) {
        this.listener = listener;
        this.pages = pages;
    }

    public void setPages(Pages pages) {
        this.pages = pages;
    }

    @Override
    protected RecyclerView createView(ViewGroup container, int position) {
        Log.d("VPA", "createView: " + position);

        RecyclerView view = (RecyclerView) LayoutInflater.from(container.getContext()).inflate(R.layout.view_page, container, false);
        view.setLayoutManager(new LinearLayoutManager(container.getContext()));
        view.setItemAnimator(null); // workaround for bug - https://code.google.com/p/android/issues/detail?id=204277

        return view;
    }

    @Override
    protected void bindView(RecyclerView view, final int position) {
        Log.d("VPA", "bindView: " + position);

        view.swapAdapter(new PageItemsAdapter(listener, pages.get(position)), false);
    }

    @Override
    public int getCount() {
        return pages.size();
    }

}
