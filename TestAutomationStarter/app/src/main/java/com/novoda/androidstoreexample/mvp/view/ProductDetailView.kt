package com.novoda.androidstoreexample.mvp.view

import com.novoda.androidstoreexample.models.Product

interface ProductDetailView: BaseView {
    fun populateProduct(product: Product)
}