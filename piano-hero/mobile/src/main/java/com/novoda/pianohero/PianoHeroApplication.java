package com.novoda.pianohero;

import android.app.Application;
import android.widget.Toast;

public class PianoHeroApplication extends Application {

    private static Toast toast;

    private static Application application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    public static void popToast(String message) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(application, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
