package com.novoda.viewpageradapter.demo;

import java.util.ArrayList;
import java.util.Collections;

class Pages extends ArrayList<Page> {

    Pages(Page... pages) {
        Collections.addAll(this, pages);
    }

    public Pages copyButToggleFavoriteFor(Item item) {
        Pages pages = new Pages();
        for (Page page : this) {
            if (page.contains(item)) {
                pages.add(page.copyButToggleFavoriteFor(item));
            } else {
                pages.add(page.copy());
            }
        }
        return pages;
    }

}
