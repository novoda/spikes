package com.novoda.tpbot.bot;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.novoda.notils.logger.toast.Toaster;
import com.novoda.tpbot.FeatureSelectionController;
import com.novoda.tpbot.bot.device.ConnectedDevicesFetcher;
import com.novoda.tpbot.bot.device.DeviceConnection;

import dagger.Module;
import dagger.Provides;

@Module
public class BotModule {

    @Provides
    DeviceConnection.DeviceConnectionListener provideHumanView(BotActivity botActivity) {
        return botActivity;
    }

    @Provides
    FeatureSelectionController<Menu, MenuItem> provideBotMenuFeatureSelectionController(MenuInflater menuInflater,
                                                                                        Toaster toaster,
                                                                                        ConnectedDevicesFetcher connectedDevicesFetcher) {
        return new BotMenuFeatureSelectionController(menuInflater, toaster, connectedDevicesFetcher);
    }

}
