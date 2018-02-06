package com.novoda.androidstoreexample.dagger.component

import dagger.Component
import com.novoda.androidstoreexample.dagger.categoryList.CategoryListComponent
import com.novoda.androidstoreexample.dagger.categoryList.CategoryListModule
import com.novoda.androidstoreexample.dagger.categoryList.ProductListComponent
import com.novoda.androidstoreexample.dagger.categoryList.ProductListModule
import com.novoda.androidstoreexample.dagger.module.*
import com.novoda.androidstoreexample.dagger.productDetails.ProductDetailsComponent
import com.novoda.androidstoreexample.dagger.productDetails.ProductDetailsModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, HostModule::class, RetrofitModule::class, ClientModule::class, BasketModule::class))
interface AppComponent {
    fun injectCategory(categoryListModule: CategoryListModule): CategoryListComponent
    fun injectProducts(productListModule: ProductListModule): ProductListComponent
    fun injectProductDetails(productDetailsModule: ProductDetailsModule): ProductDetailsComponent
}