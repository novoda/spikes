package com.novoda.tpbot;

import android.support.annotation.MenuRes;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.Arrays;
import java.util.List;

public class MenuFeatureSelectionController implements FeatureSelectionController<Menu, MenuItem> {

    @MenuRes
    private static final int FEATURE_MENU_RESOURCE = R.menu.feature_menu;

    private static final List<Integer> MENU_ITEMS = Arrays.asList(
            R.id.video_call_menu_item,
            R.id.server_connection_menu_item
    );

    private final MenuInflater menuInflater;

    public MenuFeatureSelectionController(MenuInflater menuInflater) {
        this.menuInflater = menuInflater;
    }

    @Override
    public void attachFeatureSelectionTo(Menu toAttachTo) {
        menuInflater.inflate(FEATURE_MENU_RESOURCE, toAttachTo);
    }

    @Override
    public void handleFeatureToggle(MenuItem featureRepresentation) {
        for (Integer menuItem : MENU_ITEMS) {
            if (menuItem.equals(featureRepresentation.getItemId())) {
                featureRepresentation.setChecked(!featureRepresentation.isChecked());
                featureRepresentation.setIcon(from(featureRepresentation.isChecked()));
            }
        }
    }

    private int from(boolean isChecked) {
        return isChecked ? android.R.drawable.checkbox_on_background : android.R.drawable.checkbox_off_background;
    }

    @Override
    public boolean contains(MenuItem featureRepresentation) {
        return MENU_ITEMS.contains(featureRepresentation.getItemId());
    }

}
