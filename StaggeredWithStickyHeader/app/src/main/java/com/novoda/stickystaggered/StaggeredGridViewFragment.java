package com.novoda.stickystaggered;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;

public class StaggeredGridViewFragment extends Fragment {

    private static final String HEADER_VIEW_OFFSET = "com.novoda.OFFSET_KEY";
    private static final int HEADER_HEIGHT_MINUS_TABS = 400;
    private static final int TOTAL_HEADER_HEIGHT = 500;

    private View headerPlaceholder;
    private View headerContent;
    private NovoStaggeredGridView grid;

    public StaggeredGridViewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        headerContent = rootView.findViewById(R.id.header_content);

        grid = (NovoStaggeredGridView) rootView.findViewById(R.id.grid_view);
        grid.setOnScrollListener(makeScrollListener());

        headerPlaceholder = new View(getActivity());
        headerPlaceholder.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, TOTAL_HEADER_HEIGHT));
        headerPlaceholder.setBackgroundColor(Color.RED);
        grid.addHeaderView(headerPlaceholder);

        grid.setAdapter(new ImagesAdapter(getActivity()));
        return rootView;
    }

    private AbsListView.OnScrollListener makeScrollListener() {
        return new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                updateHeaderContentOffset(getHeaderContentOffset());
            }
        };
    }

    private void updateHeaderContentOffset(int translationY) {
        headerContent.setTranslationY(translationY);
    }

    private int getHeaderContentOffset() {
        if (headerPlaceholder == null) {
            return 0;
        }
        if (isNotVisibleOrHasNotBeenLayoutYet()) {
            return -HEADER_HEIGHT_MINUS_TABS;
        }
        return Math.max(-HEADER_HEIGHT_MINUS_TABS, Math.min(0, headerPlaceholder.getTop()));
    }

    private boolean isNotVisibleOrHasNotBeenLayoutYet() {
        return headerPlaceholder.getWidth() == 0 || headerPlaceholder.getHeight() == 0;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && grid.getViewTreeObserver() != null) {
            grid.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    grid.getViewTreeObserver().removeOnPreDrawListener(this);
                    if (savedInstanceState.containsKey(HEADER_VIEW_OFFSET)) {
                        grid.setOffsetY(savedInstanceState.getInt(HEADER_VIEW_OFFSET));
                    }
                    updateHeaderContentOffset(getHeaderContentOffset());
                    return true;
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        int offset = headerPlaceholder.getTop();
        if (isHeaderVisible(offset)) {
            // StaggeredGridView resets scroll position when a header view is visible and the device rotates, so in that case we save and restore the scroll position ourselves
            outState.putInt(HEADER_VIEW_OFFSET, offset);
        }
        super.onSaveInstanceState(outState);
    }

    private boolean isHeaderVisible(int offset) {
        return offset >= -TOTAL_HEADER_HEIGHT;
    }
}
