package com.novoda.viewpageradapter.demo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

public class DemoActivity extends Activity implements ItemClickListener {

    private PagesAdapter pagerAdapter;
    private Pages pages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        pages = new Pages(
                Page.newInstance(0, 20, Color.RED),
                Page.newInstance(1, 8, Color.GREEN),
                Page.newInstance(2, 12, Color.BLUE),
                Page.newInstance(3, 25, Color.MAGENTA),
                Page.newInstance(4, 17, Color.YELLOW),
                Page.newInstance(5, 23, Color.CYAN)
        );

        pagerAdapter = new PagesAdapter(this, pages);
        ((ViewPager) findViewById(R.id.pager)).setAdapter(pagerAdapter);
    }

    @Override
    public void onClick(Item item) {
        this.pages = pages.copyButToggleFavoriteFor(item);

        pagerAdapter.setPages(pages);
        pagerAdapter.notifyDataSetChanged();
    }

}
