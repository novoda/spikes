package com.novoda.accessibility.demo.base;

import android.app.Activity;

public class Demo {

    public final String label;
    public final Class<? extends Activity> activityClass;

    public Demo(String label, Class<? extends Activity> activityClass) {
        this.label = label;
        this.activityClass = activityClass;
    }

}
