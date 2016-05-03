package com.odai.architecturedemo.cat

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.odai.architecturedemo.CatApplication
import com.odai.architecturedemo.R
import com.odai.architecturedemo.cat.view.CatView
import com.odai.architecturedemo.cats.CatsPresenter
import com.odai.architecturedemo.cats.view.CatsView
import com.odai.architecturedemo.loading.LoadingView
import com.odai.architecturedemo.navigation.AndroidNavigator
import kotlinx.android.synthetic.main.activity_cat.*

class CatActivity : AppCompatActivity() {

    private var _catPresenter: CatPresenter? = null

    private var catPresenter: CatPresenter
        get() = _catPresenter!!
        set(value) {
            _catPresenter = value
        };

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cat)
        val id = intent.getIntExtra(AndroidNavigator.ID_EXTRA, -1)
        if(id == -1) throw IllegalArgumentException("Intent should contain the cat id")
        catPresenter = CatPresenter(id, getCatApplication().catService, content, loadingView)
    }

    private fun getCatApplication(): CatApplication {
        return application as CatApplication
    }

    override fun onResume() {
        super.onResume()
        catPresenter.startPresenting()
    }

    override fun onPause() {
        super.onPause()
        catPresenter.stopPresenting()
    }

}
