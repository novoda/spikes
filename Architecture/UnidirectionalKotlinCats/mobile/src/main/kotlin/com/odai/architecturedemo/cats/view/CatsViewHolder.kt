package com.odai.architecturedemo.cats.view

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.odai.architecturedemo.R
import com.odai.architecturedemo.cats.CatsPresenter
import com.odai.architecturedemo.cat.model.Cat
import com.odai.architecturedemo.favourite.model.FavouriteState

class CatsViewHolder(itemView: CatEntryView) : RecyclerView.ViewHolder(itemView) {

    fun bind(cat: Cat, favouriteState: FavouriteState, listener: CatsPresenter.CatClickedListener) {
        (itemView as CatEntryView).display(cat, favouriteState, listener)
    }

}
