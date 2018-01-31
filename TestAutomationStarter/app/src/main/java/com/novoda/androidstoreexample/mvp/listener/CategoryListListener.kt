package com.novoda.androidstoreexample.mvp.listener

import com.novoda.androidstoreexample.models.CategoryResponse

interface CategoryListListener: BaseListener {

    fun onSuccess(categoryResponse: CategoryResponse)
}