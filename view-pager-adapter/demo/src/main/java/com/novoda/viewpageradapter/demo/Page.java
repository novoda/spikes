package com.novoda.viewpageradapter.demo;

import android.support.annotation.ColorInt;

import java.util.ArrayList;
import java.util.List;

class Page {

    private final int pageNumber;
    private final List<Item> items;

    static Page newInstance(int pageNumber, int size, @ColorInt int color) {
        ArrayList<Item> items = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            items.add(Item.newInstance(pageNumber, i, color));
        }
        return new Page(pageNumber, items);
    }

    Page copy() {
        List<Item> itemCopies = new ArrayList<>();
        for (Item old : items) {
            itemCopies.add(old.copy());
        }
        return new Page(pageNumber, itemCopies);
    }

    Page copyButToggleFavoriteFor(Item item) {
        List<Item> itemCopies = new ArrayList<>();
        for (Item old : items) {
            if (old.equals(item)) {
                itemCopies.add(old.copyButToggleFavorite());
            } else {
                itemCopies.add(old.copy());
            }
        }
        return new Page(pageNumber, itemCopies);
    }

    private Page(int pageNumber, List<Item> items) {
        this.pageNumber = pageNumber;
        this.items = items;
    }

    public int size() {
        return items.size();
    }

    public Item getItem(int position) {
        return items.get(position);
    }

    boolean contains(Item item) {
        return items.contains(item);
    }

}
