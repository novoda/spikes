package com.novoda.androidskeleton;

import android.content.Context;
import android.icu.util.VersionInfo;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class PeekableMoreThingsView extends LinearLayout {

    private RecyclerView recyclerView;

    public PeekableMoreThingsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        super.setOrientation(VERTICAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_peekable_more_things, this);
        recyclerView = ((RecyclerView) findViewById(R.id.more_things_recyclerview));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void bind(MoreThings moreThings) {
        RowsAdapter rowsAdapter = new RowsAdapter(moreThings.rows);
        recyclerView.setAdapter(rowsAdapter);
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
