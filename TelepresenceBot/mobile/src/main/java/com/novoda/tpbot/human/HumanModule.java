package com.novoda.tpbot.human;

import dagger.Module;
import dagger.Provides;

@Module
public class HumanModule {

    @Provides
    HumanTelepresenceService provideHumanTelepresenceService() {
        return new SocketIOTelepresenceService();
    }

}
