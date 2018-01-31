package com.novoda.androidstoreexample.activities

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.novoda.androidstoreexample.dagger.App
import com.novoda.androidstoreexample.dagger.component.AppComponent
import com.novoda.androidstoreexample.mvp.view.BaseView

abstract class BaseActivity: AppCompatActivity(), BaseView {

    abstract fun getActivityLayout(): Int

    abstract fun injectDependencies(appComponent: AppComponent)

    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getActivityLayout())
        injectDependencies(App.component)
        initProgressDialog()
    }

    private fun initProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog?.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog?.setMessage("Please Wait")
    }

    override fun showProgress() {
        progressDialog?.show()
    }

    override fun hideProgress() {
        progressDialog?.hide()
    }

    override fun showMessage(message: String) {

    }
}