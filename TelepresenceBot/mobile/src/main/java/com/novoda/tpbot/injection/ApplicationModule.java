package com.novoda.tpbot.injection;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.hardware.usb.UsbManager;
import android.preference.PreferenceManager;
import android.view.MenuInflater;
import android.view.accessibility.AccessibilityManager;

import com.novoda.notils.logger.toast.Toaster;
import com.novoda.tpbot.LastServerPersistence;
import com.novoda.tpbot.LastServerPreferences;
import com.novoda.tpbot.bot.BotSubcomponent;
import com.novoda.tpbot.bot.service.ServiceConnectionFeature;
import com.novoda.tpbot.bot.video.calling.VideoCallFeature;
import com.novoda.tpbot.human.HumanSubcomponent;
import com.novoda.tpbot.landing.LandingSubcomponent;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(subcomponents = {LandingSubcomponent.class, HumanSubcomponent.class, BotSubcomponent.class})
public class ApplicationModule {

    @Provides
    @Singleton
    Context provideContext(Application application) {
        return application.getApplicationContext();
    }

    @Provides
    SharedPreferences provideSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    LastServerPersistence provideLastServerPersistence(SharedPreferences sharedPreferences) {
        return new LastServerPreferences(sharedPreferences);
    }

    @Provides
    UsbManager provideUsbManager(Context context) {
        return (UsbManager) context.getSystemService(Context.USB_SERVICE);
    }

    @Provides
    Resources provideResources(Context context) {
        return context.getResources();
    }

    @Provides
    Toaster provideToaster(Context context) {
        return Toaster.newInstance(context);
    }

    @Provides
    MenuInflater provideMenuInflater(Context context) {
        return new MenuInflater(context);
    }

    @Provides
    AccessibilityManager provideAccessibilityManager(Context context) {
        return (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
    }

    @Provides
    ServiceConnectionFeature provideServiceConnectionFeature(Context context) {
        String serverConnectionPreferenceName = "server_connection";
        SharedPreferences sharedPreferences = context.getSharedPreferences(serverConnectionPreferenceName, Context.MODE_PRIVATE);
        return new ServiceConnectionFeature(sharedPreferences);
    }

    @Provides
    VideoCallFeature provideVideoCallFeature(Context context) {
        String videoCallPreferenceName = "video_call";
        SharedPreferences sharedPreferences = context.getSharedPreferences(videoCallPreferenceName, Context.MODE_PRIVATE);
        return new VideoCallFeature(sharedPreferences);
    }

}
