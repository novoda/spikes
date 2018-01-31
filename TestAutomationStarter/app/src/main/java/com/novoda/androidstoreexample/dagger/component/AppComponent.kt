package com.novoda.androidstoreexample.dagger.component

import dagger.Component
import com.novoda.androidstoreexample.dagger.*
import com.novoda.androidstoreexample.dagger.module.AppModule
import com.novoda.androidstoreexample.dagger.module.ClientModule
import com.novoda.androidstoreexample.dagger.module.HostModule
import com.novoda.androidstoreexample.dagger.module.RetrofitModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, HostModule::class, RetrofitModule::class, ClientModule::class))
interface AppComponent {
    fun plus(categoryListModule: CategoryListModule): CategoryListComponent
}