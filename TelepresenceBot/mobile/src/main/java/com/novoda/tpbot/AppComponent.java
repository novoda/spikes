package com.novoda.tpbot;

import android.app.Application;

import com.novoda.tpbot.injection.ApplicationModule;
import com.novoda.tpbot.injection.InjectorsModule;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = {ApplicationModule.class, InjectorsModule.class})
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);
        Builder applicationModule(ApplicationModule applicationModule);
        AppComponent build();
    }

    void inject(TelepresenceBotApplication application);

}

