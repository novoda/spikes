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
import com.novoda.tpbot.FeaturePersistenceFactory;
import com.novoda.tpbot.LastServerPersistence;
import com.novoda.tpbot.LastServerPreferences;

import androidx.annotation.NonNull;
import dagger.Module;
import dagger.Provides;

@Module()
public class ApplicationModule {

    private Context context;

    public ApplicationModule(@NonNull Application application) {
        this.context = application;
    }

    @Provides
    Context context() {
        return context;
    }

    @Provides
    SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    LastServerPersistence provideLastServerPersistence(SharedPreferences sharedPreferences) {
        return new LastServerPreferences(sharedPreferences);
    }

    @Provides
    UsbManager provideUsbManager() {
        return (UsbManager) context.getSystemService(Context.USB_SERVICE);
    }

    @Provides
    Resources provideResources() {
        return context.getResources();
    }

    @Provides
    Toaster provideToaster() {
        return Toaster.newInstance(context);
    }

    @Provides
    MenuInflater provideMenuInflater() {
        return new MenuInflater(context);
    }

    @Provides
    AccessibilityManager provideAccessibilityManager() {
        return (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
    }

    @Provides
    FeaturePersistenceFactory featurePersistenceFactory() {
        return new FeaturePersistenceFactory(context);
    }

}
