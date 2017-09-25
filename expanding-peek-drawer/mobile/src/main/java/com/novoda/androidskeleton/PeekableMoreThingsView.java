package com.novoda.androidskeleton;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.List;

public class PeekableMoreThingsView extends FrameLayout {

    private RecyclerView recyclerView;
    private Button button;
    private View moreThingsContainer;

    public PeekableMoreThingsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_peekable_more_things, this);
        moreThingsContainer = findViewById(R.id.more_things_container);
        button = (Button) findViewById(R.id.more_things_button);
        recyclerView = (RecyclerView) findViewById(R.id.more_things_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        moreThingsContainer.setVisibility(INVISIBLE);
    }

    private void updateButton(String label, View.OnClickListener onClickListener) {
        button.setText(label);
        button.setOnClickListener(onClickListener);
    }

    public void bind(MoreThings moreThings) {
        RowsAdapter rowsAdapter = new RowsAdapter(moreThings.rows);
        recyclerView.setAdapter(rowsAdapter);
    }

    public void showPeekView() {
        int peekViewWidth = getResources().getDimensionPixelSize(R.dimen.peek_width);
        moreThingsContainer.setTranslationX(getWidth());
        moreThingsContainer.setVisibility(VISIBLE);
        moreThingsContainer.animate().translationXBy(-peekViewWidth);
        updateButtonWithExpandAction();
        setDismissPeekViewClickListener();
    }

    private void updateButtonWithExpandAction() {
        updateButton("<", new OnClickListener() {
            @Override
            public void onClick(View v) {
                showExpandedView();
            }
        });
    }

    private void showExpandedView() {
        moreThingsContainer.setVisibility(VISIBLE);
        moreThingsContainer.animate().translationX(0);
        updateButton("x", new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissExpandedView();
            }
        });
        removeDismissPeekViewClickListener();
    }

    private void setDismissPeekViewClickListener() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPeekView();
            }
        });
    }

    private void dismissExpandedView() {
        int peekViewWidth = getResources().getDimensionPixelSize(R.dimen.peek_width);
        int parentWidth = getWidth();
        moreThingsContainer.animate().translationX(parentWidth - peekViewWidth);
        setDismissPeekViewClickListener();
        updateButtonWithExpandAction();
    }

    private void dismissPeekView() {
        int parentWidth = getWidth();
        moreThingsContainer.animate().translationX(parentWidth);
        removeDismissPeekViewClickListener();
    }

    private void removeDismissPeekViewClickListener() {
        setOnClickListener(null);
        setClickable(false);
    }

    private static class RowsAdapter extends RecyclerView.Adapter {

        private final List<Row> rows;

        RowsAdapter(List<Row> rows) {
            this.rows = rows;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_row, parent, false);
            return new PlainViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Row row = rows.get(position);
            ((RowView) holder.itemView).update(row);
        }

        @Override
        public int getItemCount() {
            if (rows == null) {
                return 0;
            }
            return rows.size();
        }
    }
}
