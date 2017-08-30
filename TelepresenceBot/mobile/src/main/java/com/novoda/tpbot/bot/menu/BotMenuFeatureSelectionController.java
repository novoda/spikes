package com.novoda.tpbot.bot.menu;

import android.content.Context;
import android.hardware.usb.UsbManager;
import android.support.annotation.MenuRes;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.novoda.notils.logger.toast.Toaster;
import com.novoda.tpbot.FeatureSelectionController;
import com.novoda.tpbot.R;
import com.novoda.tpbot.bot.device.usb.ConnectedUsbDevicesFetcher;

public final class BotMenuFeatureSelectionController implements FeatureSelectionController<Menu, MenuItem> {

    @MenuRes
    private static final int FEATURE_MENU_RESOURCE = R.menu.bot_menu;
    @MenuRes
    private static final int FEATURE_LIST_CONNECTED_DEVICES = R.id.usb_devices_list_menu_item;

    private final MenuInflater menuInflater;
    private final Toaster toaster;
    private final ConnectedUsbDevicesFetcher connectedUsbDevicesFetcher;

    public static FeatureSelectionController<Menu, MenuItem> createFrom(Context context) {
        UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        ConnectedUsbDevicesFetcher connectedUsbDevicesFetcher = new ConnectedUsbDevicesFetcher(manager, context.getResources());
        MenuInflater menuInflater = new MenuInflater(context);
        Toaster toaster = Toaster.newInstance(context);
        return new BotMenuFeatureSelectionController(menuInflater, toaster, connectedUsbDevicesFetcher);
    }

    private BotMenuFeatureSelectionController(MenuInflater menuInflater, Toaster toaster, ConnectedUsbDevicesFetcher connectedUsbDevicesFetcher) {
        this.menuInflater = menuInflater;
        this.toaster = toaster;
        this.connectedUsbDevicesFetcher = connectedUsbDevicesFetcher;
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

        toaster.popBurntToast(connectedUsbDevicesFetcher.fetchAsString());
    }

    @Override
    public boolean contains(MenuItem featureRepresentation) {
        return featureRepresentation.getItemId() == FEATURE_LIST_CONNECTED_DEVICES;
    }

}
