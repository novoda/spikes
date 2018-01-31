package com.novoda.androidstoreexample.dagger.component

import dagger.Component
import com.novoda.androidstoreexample.dagger.categoryList.CategoryListComponent
import com.novoda.androidstoreexample.dagger.categoryList.CategoryListModule
import com.novoda.androidstoreexample.dagger.categoryList.ProductListComponent
import com.novoda.androidstoreexample.dagger.categoryList.ProductListModule
import com.novoda.androidstoreexample.dagger.module.AppModule
import com.novoda.androidstoreexample.dagger.module.ClientModule
import com.novoda.androidstoreexample.dagger.module.HostModule
import com.novoda.androidstoreexample.dagger.module.RetrofitModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, HostModule::class, RetrofitModule::class, ClientModule::class))
interface AppComponent {
    fun injectCategory(categoryListModule: CategoryListModule): CategoryListComponent
    fun injectProducts(productListModule: ProductListModule): ProductListComponent
}