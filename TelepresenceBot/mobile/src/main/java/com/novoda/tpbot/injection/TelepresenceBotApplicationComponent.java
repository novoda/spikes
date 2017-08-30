package com.novoda.tpbot.injection;

import com.novoda.tpbot.TelepresenceBotApplication;

import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;

@Component(modules = {AndroidInjectionModule.class, ActivityBindingModule.class})
public interface TelepresenceBotApplicationComponent extends AndroidInjector<TelepresenceBotApplication> {
}
