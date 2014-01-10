package com.novoda.stickyheaderstest;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.GridView;

public class StaggeredGridViewFragment extends Fragment {

    private static final String HEADER_VIEW_OFFSET = "com.novoda.OFFSET_KEY";
    private static final int HEADER_HEIGHT_MINUS_TABS = 400;

    private View headerContent;
    private GridView grid;

    public StaggeredGridViewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        headerContent = rootView.findViewById(R.id.header_content);

        grid = (GridView) rootView.findViewById(R.id.grid_view);
        grid.setOnScrollListener(makeScrollListener());

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
        if (isNotVisibleOrHasNotBeenLayoutYet()) {
            return -HEADER_HEIGHT_MINUS_TABS;
        }

        int firstVisiblePosition = grid.getFirstVisiblePosition();
        return Math.max(-HEADER_HEIGHT_MINUS_TABS, Math.min(0, grid.getChildAt(firstVisiblePosition).getTop()));
    }

    private boolean isNotVisibleOrHasNotBeenLayoutYet() {
        return grid.getFirstVisiblePosition() > 0 || grid.getChildAt(grid.getFirstVisiblePosition()) == null;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && grid.getViewTreeObserver() != null) {
            grid.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    grid.getViewTreeObserver().removeOnPreDrawListener(this);
                    updateHeaderContentOffset(getHeaderContentOffset());
                    return true;
                }
            });
        }
    }
}
