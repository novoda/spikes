package com.odai.architecturedemo.cats.service

import com.odai.architecturedemo.cats.model.Cats
import com.odai.architecturedemo.favourite.model.FavouriteCats

class AlwaysOutdatedCatsFreshnessChecker: CatsFreshnessChecker {

    override fun isFresh(cats: Cats): Boolean {
        return false
    }

    override fun isFresh(cats: FavouriteCats): Boolean {
        return false
    }

}
