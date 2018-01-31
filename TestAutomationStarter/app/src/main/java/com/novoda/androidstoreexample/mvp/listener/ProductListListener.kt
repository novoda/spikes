package com.novoda.androidstoreexample.mvp.listener

import com.novoda.androidstoreexample.models.ProductResponse

interface ProductListListener : BaseListener {

    fun onSuccess(productResponse: ProductResponse)

}