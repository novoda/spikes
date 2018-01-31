package com.novoda.androidstoreexample.activities

import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.novoda.androidstoreexample.R
import com.novoda.androidstoreexample.adapters.ProductListAdapter
import com.novoda.androidstoreexample.utilities.CATEGORY_NAME_EXTRA
import io.reactivex.disposables.CompositeDisposable

class ProductListActivity : AppCompatActivity() {

    private lateinit var adapter: ProductListAdapter
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        val category = intent.getStringExtra(CATEGORY_NAME_EXTRA)

        val orientation = resources.configuration.orientation
        val screenResolution = resources.configuration.screenWidthDp

        var spanCount = when (orientation) {
            Configuration.ORIENTATION_PORTRAIT -> 2
            Configuration.ORIENTATION_LANDSCAPE -> 3
            else -> 0
        }

        if (screenResolution > 720) {
            spanCount = 3
        }
//        compositeDisposable.add(
//                repository.requestProductsForCategory(category.toLowerCase())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribeOn(Schedulers.io())
//                        .subscribe({ result ->
//                            if (result.products.isNotEmpty()) {
//                                adapter = ProductListAdapter(context = this,
//                                        products = result.products.toList()) { _: Product ->
                                    //            val productDetailsIntent = Intent(this, ProductDetailsActivity::class.java)
//            productDetailsIntent.putExtra(EXTRA_PRODUCT_DETAILS, product)
//            startActivity(productDetailsIntent)
//                                }
//                                val layoutManager = GridLayoutManager(this, spanCount)
//                                productListView.layoutManager = layoutManager
//                                productListView.adapter = adapter
//                            } else {
//                                noItemsVisibleTextView.visibility = VISIBLE
//                            }
//
//                        }
//                        )
//        )
    }
}

