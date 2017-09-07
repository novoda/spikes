package com.novoda.tpbot.bot;

import android.content.Context;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.accessibility.AccessibilityManager;

import com.novoda.notils.caster.Views;
import com.novoda.notils.logger.toast.Toaster;
import com.novoda.support.SelfDestructingMessageView;
import com.novoda.tpbot.FeatureSelectionController;
import com.novoda.tpbot.R;
import com.novoda.tpbot.ServiceDeclarationListener;
import com.novoda.tpbot.bot.device.ConnectedDevicesFetcher;
import com.novoda.tpbot.bot.device.DeviceConnection;
import com.novoda.tpbot.bot.movement.MovementServiceBinder;
import com.novoda.tpbot.bot.service.BotServiceBinder;
import com.novoda.tpbot.bot.video.calling.AutomationChecker;
import com.novoda.tpbot.controls.CommandRepeater;
import com.novoda.tpbot.controls.ControllerListener;
import com.novoda.tpbot.controls.ControllerView;
import com.novoda.tpbot.controls.ServerDeclarationView;
import com.novoda.tpbot.controls.SwitchableView;

import dagger.Module;
import dagger.Provides;

@Module
public class BotModule {

    @Provides
    DeviceConnection.DeviceConnectionListener provideHumanView(BotActivity botActivity) {
        return botActivity;
    }

    @Provides
    SelfDestructingMessageView provideSelfDestructingMessageView(BotActivity botActivity) {
        return Views.findById(botActivity, R.id.bot_controller_debug_view);
    }

    @Provides
    SwitchableView provideSwitchableView(BotActivity botActivity) {
        return Views.findById(botActivity, R.id.bot_switchable_view);
    }

    @Provides
    ControllerListener provideControllerListener(BotActivity botActivity) {
        return botActivity;
    }

    @Provides
    ControllerView provideControllerView(SwitchableView switchableView, ControllerListener controllerListener) {
        ControllerView controllerView = Views.findById(switchableView, R.id.bot_controller_direction_view);
        controllerView.setControllerListener(controllerListener);
        return controllerView;
    }

    @Provides
    ServiceDeclarationListener provideServiceDeclarationListener(BotActivity botActivity) {
        return botActivity;
    }

    @Provides
    ServerDeclarationView provideServerDeclarationView(SwitchableView switchableView, ServiceDeclarationListener serviceDeclarationListener) {
        ServerDeclarationView serverDeclarationView = Views.findById(switchableView, R.id.bot_server_declaration_view);
        serverDeclarationView.setServiceDeclarationListener(serviceDeclarationListener);
        return serverDeclarationView;
    }

    @Provides
    CommandRepeater.Listener provideCommandRepeaterListener(BotActivity botActivity) {
        return botActivity;
    }

    @Provides
    CommandRepeater provideCommandRepeater(CommandRepeater.Listener listener) {
        return new CommandRepeater(listener, new Handler());
    }

    @Provides
    FeatureSelectionController<Menu, MenuItem> provideBotMenuFeatureSelectionController(MenuInflater menuInflater,
                                                                                        Toaster toaster,
                                                                                        ConnectedDevicesFetcher connectedDevicesFetcher) {
        return new BotMenuFeatureSelectionController(menuInflater, toaster, connectedDevicesFetcher);
    }

    @Provides
    AutomationChecker provideAutomationChecker(AccessibilityManager accessibilityManager) {
        return new AutomationChecker(accessibilityManager);
    }

    @Provides
    BotServiceBinder provideBotServiceBinder(Context context) {
        return new BotServiceBinder(context);
    }

    @Provides
    MovementServiceBinder provideMovementServiceBinder(Context context, DeviceConnection deviceConnection) {
        return new MovementServiceBinder(context, deviceConnection);
    }

}
