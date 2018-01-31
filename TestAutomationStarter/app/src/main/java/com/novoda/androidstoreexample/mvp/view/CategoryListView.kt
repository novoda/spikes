package com.novoda.androidstoreexample.mvp.view

import com.novoda.androidstoreexample.models.Category


interface CategoryListView : BaseView{

    fun showCategoryList(categories: List<Category>)

    fun onItemClicked(type: String)
}