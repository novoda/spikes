package com.novoda.tpbot.bot;

import com.novoda.tpbot.bot.device.usb.UsbDeviceModule;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

@Subcomponent(modules = {BotModule.class, UsbDeviceModule.class})
public interface BotSubcomponent extends AndroidInjector<BotActivity> {

    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<BotActivity> {
    }

}
