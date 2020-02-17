package com.novoda.tpbot.human;

import android.os.Handler;

import com.novoda.tpbot.LastServerPersistence;
import com.novoda.tpbot.controls.CommandRepeater;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public abstract class HumanModule {

    @Binds
    abstract CommandRepeater.Listener provideCommandRepeaterListener(HumanActivity humanActivity);

    @Provides
    static CommandRepeater provideCommandRepeater(CommandRepeater.Listener listener) {
        return new CommandRepeater(listener, new Handler());
    }

    @Binds
    abstract HumanView provideHumanView(HumanActivity humanActivity);

    @Provides
    static HumanTelepresenceService provideHumanTelepresenceService() {
        return new SocketIOTelepresenceService();
    }

    @Provides
    static HumanPresenter provideHumanPresenter(HumanTelepresenceService humanTelepresenceService,
                                                HumanView humanView,
                                                LastServerPersistence lastServerPersistence) {
        return new HumanPresenter(humanTelepresenceService, humanView, lastServerPersistence);
    }

}
