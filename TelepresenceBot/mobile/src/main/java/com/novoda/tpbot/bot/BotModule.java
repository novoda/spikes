package com.novoda.tpbot.bot;

import com.novoda.tpbot.bot.device.DeviceConnection;

import dagger.Module;
import dagger.Provides;

@Module
public class BotModule {

    @Provides
    DeviceConnection.DeviceConnectionListener provideHumanView(BotActivity botActivity) {
        return botActivity;
    }

}
