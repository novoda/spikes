package com.odai.architecturedemo.cats.service

import com.odai.architecturedemo.cats.model.Cats
import com.odai.architecturedemo.favourite.model.FavouriteCats

interface CatsFreshnessChecker {

    fun isFresh(cats: Cats): Boolean

    fun isFresh(cats: FavouriteCats): Boolean

}
