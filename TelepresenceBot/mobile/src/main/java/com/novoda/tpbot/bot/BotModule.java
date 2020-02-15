package com.novoda.tpbot.bot;

import android.content.Context;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.accessibility.AccessibilityManager;

import com.novoda.notils.logger.toast.Toaster;
import com.novoda.tpbot.FeatureSelectionController;
import com.novoda.tpbot.bot.device.ConnectedDevicesFetcher;
import com.novoda.tpbot.bot.device.DeviceConnection;
import com.novoda.tpbot.bot.movement.MovementServiceBinder;
import com.novoda.tpbot.bot.video.calling.AutomationChecker;
import com.novoda.tpbot.controls.ActionRepeater;
import com.novoda.tpbot.threading.MyLooperDelayedExecutor;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public abstract class BotModule {

    @Binds
    abstract DeviceConnection.DeviceConnectionListener provideDeviceConnectionListener(BotActivity botActivity);

    @Binds
    abstract ActionRepeater.Listener provideCommandRepeaterListener(BotActivity botActivity);

    @Provides
    static ActionRepeater provideCommandRepeater(ActionRepeater.Listener listener) {
        return new ActionRepeater(listener, MyLooperDelayedExecutor.newInstance());
    }

    @Provides
    static FeatureSelectionController<Menu, MenuItem> provideBotMenuFeatureSelectionController(MenuInflater menuInflater,
                                                                                               Toaster toaster,
                                                                                               ConnectedDevicesFetcher connectedDevicesFetcher) {
        return new BotMenuFeatureSelectionController(menuInflater, toaster, connectedDevicesFetcher);
    }

    @Provides
    static AutomationChecker provideAutomationChecker(AccessibilityManager accessibilityManager) {
        return new AutomationChecker(accessibilityManager);
    }

    @Provides
    static BotServiceBinder provideBotServiceBinder(Context context) {
        return new BotServiceBinder(context);
    }

    @Provides
    static MovementServiceBinder provideMovementServiceBinder(Context context, DeviceConnection deviceConnection) {
        return new MovementServiceBinder(context, deviceConnection);
    }

}
