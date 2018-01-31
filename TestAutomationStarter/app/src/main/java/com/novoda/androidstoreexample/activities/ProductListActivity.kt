package com.novoda.androidstoreexample.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.widget.GridLayout
import com.novoda.androidstoreexample.R
import com.novoda.androidstoreexample.adapters.ProductListAdapter
import com.novoda.androidstoreexample.dagger.categoryList.CategoryListModule
import com.novoda.androidstoreexample.dagger.categoryList.ProductListModule
import com.novoda.androidstoreexample.dagger.component.AppComponent
import com.novoda.androidstoreexample.models.Product
import com.novoda.androidstoreexample.mvp.presenter.ProductListPresenter
import com.novoda.androidstoreexample.mvp.view.ProductListView
import com.novoda.androidstoreexample.utilities.CATEGORY_NAME_EXTRA
import kotlinx.android.synthetic.main.activity_product_list.*
import javax.inject.Inject

class ProductListActivity : BaseActivity(), ProductListView {

    private lateinit var productListAdapter: ProductListAdapter

    @Inject
    lateinit var presenter: ProductListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val category = intent.getIntExtra(CATEGORY_NAME_EXTRA, -1)
        presenter.loadProductList(category)
    }

    override fun showProductList(products: List<Product>) {
        productListView.layoutManager = GridLayoutManager(this, 2)
        productListAdapter = ProductListAdapter(this, products) {

        }
        productListView.adapter = productListAdapter
    }

    override fun getActivityLayout(): Int {
        return R.layout.activity_product_list
    }

    override fun injectDependencies(appComponent: AppComponent) {
        appComponent.injectProducts(ProductListModule(this)).inject(this)
    }
}
