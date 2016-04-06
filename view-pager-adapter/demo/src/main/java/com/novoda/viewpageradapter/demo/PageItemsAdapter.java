package com.novoda.viewpageradapter.demo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

class PageItemsAdapter extends RecyclerView.Adapter<PageItemsAdapter.ItemViewHolder> {

    private final ItemClickListener listener;
    private final Page page;

    PageItemsAdapter(ItemClickListener listener, Page page) {
        this.listener = listener;
        this.page = page;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return page.getItem(position).id();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ItemViewHolder.inflate(parent);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.bind(page.getItem(position), listener);
    }

    @Override
    public int getItemCount() {
        return page.size();
    }

    static final class ItemViewHolder extends RecyclerView.ViewHolder {

        private final View colorView;
        private final TextView pageTextView;
        private final TextView itemTextView;

        static ItemViewHolder inflate(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.view_item, parent, false);
            View colorView = view.findViewById(R.id.color);
            TextView pageTextView = (TextView) view.findViewById(R.id.page_number);
            TextView itemTextView = (TextView) view.findViewById(R.id.item_text);
            return new ItemViewHolder(view, colorView, pageTextView, itemTextView);
        }

        private ItemViewHolder(View itemView, View colorView, TextView pageTextView, TextView itemTextView) {
            super(itemView);
            this.colorView = colorView;
            this.pageTextView = pageTextView;
            this.itemTextView = itemTextView;
        }

        public void bind(final Item item, final ItemClickListener listener) {
            colorView.setBackgroundColor(item.color());
            pageTextView.setText(String.valueOf(item.pageNumber()));
            itemTextView.setText(item.label());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(item);
                }
            });
        }

    }

}
