package com.novoda.tpbot.injection;

import android.app.Application;

import com.novoda.tpbot.TelepresenceBotApplication;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

@Singleton
@Component(modules = {AndroidInjectionModule.class, ActivityBindingModule.class, ApplicationModule.class})
public interface TelepresenceBotApplicationComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        TelepresenceBotApplicationComponent build();
    }

    void inject(TelepresenceBotApplication application);
}
