package com.novoda.tpbot.human;

import android.os.Handler;

import com.novoda.notils.caster.Views;
import com.novoda.support.SelfDestructingMessageView;
import com.novoda.support.SwitchableView;
import com.novoda.tpbot.LastServerPersistence;
import com.novoda.tpbot.R;
import com.novoda.tpbot.controls.CommandRepeater;
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
    SelfDestructingMessageView provideSelfDestructingMessageView(HumanActivity humanActivity) {
        return Views.findById(humanActivity, R.id.bot_controller_debug_view);
    }

    @Provides
    SwitchableView provideSwitchableView(HumanActivity humanActivity) {
        return Views.findById(humanActivity, R.id.bot_switchable_view);
    }

    @Provides
    ControllerView provideControllerView(SwitchableView switchableView) {
        return Views.findById(switchableView, R.id.bot_controller_direction_view);
    }

    @Provides
    ServerDeclarationView provideServerDeclarationView(SwitchableView switchableView) {
        return Views.findById(switchableView, R.id.bot_server_declaration_view);
    }

    @Provides
    CommandRepeater provideCommandRepeater() {
        return new CommandRepeater(new Handler());
    }

    @Provides
    HumanPresenter provideHumanPresenter(HumanTelepresenceService humanTelepresenceService,
                                         HumanView humanView,
                                         LastServerPersistence lastServerPersistence) {
        return new HumanPresenter(humanTelepresenceService, humanView, lastServerPersistence);
    }

}
