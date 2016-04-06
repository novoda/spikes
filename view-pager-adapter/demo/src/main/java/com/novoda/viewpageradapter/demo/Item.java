package com.novoda.viewpageradapter.demo;

import android.support.annotation.ColorInt;

class Item {

    private final int id;
    @ColorInt private final int color;
    private final int pageNumber;
    private final boolean isFavorite;

    static Item newInstance(int pageNumber, int id, @ColorInt int color) {
        return new Item(id, color, pageNumber, false);
    }

    Item copy() {
        return new Item(id, color, pageNumber, isFavorite);
    }

    Item copyButToggleFavorite() {
        return new Item(id, color, pageNumber, !isFavorite);
    }

    private Item(int id, @ColorInt int color, int pageNumber, boolean isFavorite) {
        this.id = id;
        this.color = color;
        this.pageNumber = pageNumber;
        this.isFavorite = isFavorite;
    }

    public int id() {
        return id;
    }

    @ColorInt
    public int color() {
        return color;
    }

    public int pageNumber() {
        return pageNumber;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public String label() {
        return "item " + id + (isFavorite() ? " | fave" : " | meh");
    }

}
