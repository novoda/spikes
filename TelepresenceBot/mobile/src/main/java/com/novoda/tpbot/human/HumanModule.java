package com.novoda.tpbot.human;

import com.novoda.tpbot.LastServerPersistence;

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
    HumanPresenter provideHumanPresenter(HumanTelepresenceService humanTelepresenceService,
                                         HumanView humanView,
                                         LastServerPersistence lastServerPersistence) {
        return new HumanPresenter(humanTelepresenceService, humanView, lastServerPersistence);
    }

}
