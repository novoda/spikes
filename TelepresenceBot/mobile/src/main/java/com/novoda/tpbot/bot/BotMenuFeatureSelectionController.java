package com.novoda.tpbot.bot;

import androidx.annotation.MenuRes;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.novoda.notils.logger.toast.Toaster;
import com.novoda.tpbot.FeatureSelectionController;
import com.novoda.tpbot.R;
import com.novoda.tpbot.bot.device.ConnectedDevicesFetcher;

final class BotMenuFeatureSelectionController implements FeatureSelectionController<Menu, MenuItem> {

    @MenuRes
    private static final int FEATURE_MENU_RESOURCE = R.menu.bot_menu;
    @MenuRes
    private static final int FEATURE_LIST_CONNECTED_DEVICES = R.id.usb_devices_list_menu_item;

    private final MenuInflater menuInflater;
    private final Toaster toaster;
    private final ConnectedDevicesFetcher connectedDevicesFetcher;

    BotMenuFeatureSelectionController(MenuInflater menuInflater, Toaster toaster, ConnectedDevicesFetcher connectedDevicesFetcher) {
        this.menuInflater = menuInflater;
        this.toaster = toaster;
        this.connectedDevicesFetcher = connectedDevicesFetcher;
    }

    @Override
    public void attachFeaturesTo(Menu componentToAttachTo) {
        menuInflater.inflate(FEATURE_MENU_RESOURCE, componentToAttachTo);
    }

    @Override
    public void handleFeatureToggle(MenuItem featureRepresentation) {
        if (featureRepresentation.getItemId() != FEATURE_LIST_CONNECTED_DEVICES) {
            throw new IllegalStateException("You must check if data is present before using handleFeatureToggle().");
        }

        toaster.popBurntToast(connectedDevicesFetcher.fetchAsString());
    }

    @Override
    public boolean contains(MenuItem featureRepresentation) {
        return featureRepresentation.getItemId() == FEATURE_LIST_CONNECTED_DEVICES;
    }

}
