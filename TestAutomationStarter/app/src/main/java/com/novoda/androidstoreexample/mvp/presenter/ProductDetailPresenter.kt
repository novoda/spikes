package com.novoda.androidstoreexample.mvp.presenter

import com.novoda.androidstoreexample.models.Product

interface ProductDetailPresenter: BasePresenter {
    fun loadProductDetails(productId: Int)

    fun addToBasket(product:Product)
}