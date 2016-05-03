package com.odai.architecturedemo.loading

import android.content.Context
import android.support.design.widget.Snackbar
import android.util.AttributeSet
import android.view.View
import android.widget.*
import com.odai.architecturedemo.R
import com.odai.architecturedemo.cats.CatsPresenter
import kotlinx.android.synthetic.main.loading_view.view.*

class AndroidLoadingView(context: Context, attrs: AttributeSet) : LoadingView, FrameLayout(context, attrs) {

    private var _content: View? = null

    private var content: View
        get() = _content!!
        set(value) {
            _content = value
        };

    private var snackBar: Snackbar? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        content = findViewById(R.id.content)
    }

    override fun attach(retryListener: RetryClickedListener) {
        loadingButton.setOnClickListener {
            retryListener.onRetry()
        }
    }


    override fun showLoadingIndicator() {
        content.visibility = VISIBLE
        loadingContainer.visibility = GONE
        displaySnackBar("Still loading data")
    }

    override fun showErrorIndicator() {
        content.visibility = VISIBLE
        loadingContainer.visibility = GONE
        displaySnackBar("An error has occurred")
    }

    override fun showLoadingScreen() {
        snackBar?.dismiss()
        content.visibility = GONE
        loadingContainer.visibility = VISIBLE
        loadingLabel.text = "LOADING"
    }

    override fun showData() {
        snackBar?.dismiss()
        content.visibility = VISIBLE
        loadingContainer.visibility = GONE
    }

    override fun showEmptyScreen() {
        snackBar?.dismiss()
        content.visibility = GONE
        loadingContainer.visibility = VISIBLE
        loadingLabel.text = "EMPTY"
    }

    override fun showErrorScreen() {
        snackBar?.dismiss()
        content.visibility = GONE
        loadingContainer.visibility = VISIBLE
        loadingLabel.text = "ERROR"
    }

    private fun displaySnackBar(message: String) {
        if (snackBar?.isShown ?: false) {
            snackBar!!.setText(message)
        } else {
            snackBar = Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE)
            snackBar?.show()
        }
    }

}
