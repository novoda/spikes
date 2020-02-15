package com.novoda.tpbot.injection;

import com.novoda.tpbot.bot.BotActivity;
import com.novoda.tpbot.bot.BotModule;
import com.novoda.tpbot.bot.device.usb.UsbDeviceModule;
import com.novoda.tpbot.human.HumanActivity;
import com.novoda.tpbot.human.HumanModule;
import com.novoda.tpbot.landing.LandingActivity;
import com.novoda.tpbot.landing.LandingModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Module(includes = AndroidSupportInjectionModule.class)
public abstract class InjectorsModule {

    @ContributesAndroidInjector(modules = LandingModule.class)
    abstract LandingActivity contributeLandingAndroidInjector();

    @ContributesAndroidInjector(modules = HumanModule.class)
    abstract HumanActivity contributeHumanAndroidInjector();

    @ContributesAndroidInjector(modules = {BotModule.class, UsbDeviceModule.class})
    abstract BotActivity contributeBotAndroidInjector();

}
