package com.odai.architecturedemo.cats.view

import com.odai.architecturedemo.cats.model.Cats
import com.odai.architecturedemo.cats.CatsPresenter
import com.odai.architecturedemo.favourite.model.FavouriteCats

interface CatsView {

    fun attach(listener: CatsPresenter.CatClickedListener)

    fun display(cats: Cats)

    fun display(favouriteCats: FavouriteCats)

}
