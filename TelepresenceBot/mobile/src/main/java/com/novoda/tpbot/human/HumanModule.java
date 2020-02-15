package com.novoda.tpbot.human;

import com.novoda.tpbot.LastServerPersistence;
import com.novoda.tpbot.controls.ActionRepeater;
import com.novoda.tpbot.threading.MyLooperDelayedExecutor;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public abstract class HumanModule {

    @Binds
    abstract ActionRepeater.Listener provideCommandRepeaterListener(HumanActivity humanActivity);

    @Provides
    static ActionRepeater provideCommandRepeater(ActionRepeater.Listener listener) {
        return new ActionRepeater(listener, MyLooperDelayedExecutor.newInstance());
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
