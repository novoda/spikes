package com.odai.architecturedemo.cats.view

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.odai.architecturedemo.R
import com.odai.architecturedemo.cats.model.Cats
import com.odai.architecturedemo.cats.CatsPresenter
import com.odai.architecturedemo.favourite.model.FavouriteCats

class AndroidCatsView(context: Context, attrs: AttributeSet): CatsView, RecyclerView(context, attrs) {

    override fun onFinishInflate() {
        super.onFinishInflate()
        layoutManager = LinearLayoutManager(context)
        addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.top = resources.getDimensionPixelSize(R.dimen.activity_vertical_margin)
                outRect.bottom = resources.getDimensionPixelSize(R.dimen.activity_vertical_margin)
            }
        })
    }

    override fun attach(listener: CatsPresenter.CatClickedListener) {
        adapter = CatsAdapter(LayoutInflater.from(context), listener, Cats(emptyList()), FavouriteCats(mapOf()))
    }

    override fun display(cats: Cats) {
        var catAdapter = adapter as CatsAdapter
        catAdapter.cats = cats
        catAdapter.notifyDataSetChanged()
    }

    override fun display(favouriteCats: FavouriteCats) {
        var catAdapter = adapter as CatsAdapter
        catAdapter.favouriteCats = favouriteCats
        catAdapter.notifyDataSetChanged()
    }

}
