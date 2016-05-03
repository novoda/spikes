package com.odai.architecturedemo.cats.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.odai.architecturedemo.R
import com.odai.architecturedemo.cats.CatsPresenter
import com.odai.architecturedemo.cats.model.Cats
import com.odai.architecturedemo.favourite.model.FavouriteCats

class CatsAdapter(
        private val layoutInflater: LayoutInflater,
        private val listener: CatsPresenter.CatClickedListener,
        var cats: Cats,
        var favouriteCats: FavouriteCats
) : RecyclerView.Adapter<CatsViewHolder>() {

    override fun onBindViewHolder(p0: CatsViewHolder, p1: Int) {
        val cat = cats.get(p1)
        p0.bind(cat, favouriteCats.getStatusFor(cat), listener)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CatsViewHolder? {
        return CatsViewHolder(layoutInflater.inflate(R.layout.cat_entry_view, p0, false) as CatEntryView);
    }

    override fun getItemCount(): Int {
        return cats.size()
    }

}
