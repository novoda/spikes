package com.novoda.androidstoreexample.mvp.presenter

interface ProductDetailPresenter: BasePresenter {
    fun loadProductDetails(productId: Int)
}