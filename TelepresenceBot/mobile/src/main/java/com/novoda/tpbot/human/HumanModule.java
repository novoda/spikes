package com.novoda.tpbot.human;

import android.os.Handler;

import com.novoda.notils.caster.Views;
import com.novoda.support.SelfDestructingMessageView;
import com.novoda.tpbot.controls.SwitchableView;
import com.novoda.tpbot.LastServerPersistence;
import com.novoda.tpbot.R;
import com.novoda.tpbot.ServiceDeclarationListener;
import com.novoda.tpbot.controls.CommandRepeater;
import com.novoda.tpbot.controls.ControllerListener;
import com.novoda.tpbot.controls.ControllerView;
import com.novoda.tpbot.controls.ServerDeclarationView;

import dagger.Module;
import dagger.Provides;

@Module
public class HumanModule {

    @Provides
    HumanTelepresenceService provideHumanTelepresenceService() {
        return new SocketIOTelepresenceService();
    }

    @Provides
    HumanView provideHumanView(HumanActivity humanActivity) {
        return humanActivity;
    }

    @Provides
    ControllerListener provideControllerListener(HumanActivity humanActivity) {
        return humanActivity;
    }

    @Provides
    ServiceDeclarationListener provideServiceDeclarationListener(HumanActivity humanActivity) {
        return humanActivity;
    }

    @Provides
    CommandRepeater.Listener provideCommandRepeaterListener(HumanActivity humanActivity) {
        return humanActivity;
    }

    @Provides
    SelfDestructingMessageView provideSelfDestructingMessageView(HumanActivity humanActivity) {
        return Views.findById(humanActivity, R.id.bot_controller_debug_view);
    }

    @Provides
    SwitchableView provideSwitchableView(HumanActivity humanActivity) {
        return Views.findById(humanActivity, R.id.bot_switchable_view);
    }

    @Provides
    ControllerView provideControllerView(SwitchableView switchableView, ControllerListener controllerListener) {
        ControllerView controllerView = Views.findById(switchableView, R.id.bot_controller_direction_view);
        controllerView.setControllerListener(controllerListener);
        return controllerView;
    }

    @Provides
    ServerDeclarationView provideServerDeclarationView(SwitchableView switchableView, ServiceDeclarationListener serviceDeclarationListener) {
        ServerDeclarationView serverDeclarationView = Views.findById(switchableView, R.id.bot_server_declaration_view);
        serverDeclarationView.setServiceDeclarationListener(serviceDeclarationListener);
        return serverDeclarationView;
    }

    @Provides
    CommandRepeater provideCommandRepeater(CommandRepeater.Listener listener) {
        return new CommandRepeater(listener, new Handler());
    }

    @Provides
    HumanPresenter provideHumanPresenter(HumanTelepresenceService humanTelepresenceService,
                                         HumanView humanView,
                                         LastServerPersistence lastServerPersistence) {
        return new HumanPresenter(humanTelepresenceService, humanView, lastServerPersistence);
    }

}
