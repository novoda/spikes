package com.novoda.playground.multiplatform

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.novoda.playground.multiplatform.common.Presenter
import com.novoda.playground.multiplatform.common.Recipe
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), Presenter.View {

    private val presenter = Presenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        presenter.startPresenting(this)
    }

    override fun onStop() {
        presenter.stopPresenting()
        super.onStop()
    }

    override fun render(recipe: Recipe) {
        recipe_name.text = recipe.header.title
    }
}
