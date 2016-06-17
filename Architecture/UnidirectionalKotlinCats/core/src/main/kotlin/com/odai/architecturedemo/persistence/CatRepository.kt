package com.odai.architecturedemo.persistence

import com.odai.architecturedemo.cat.model.Cat
import com.odai.architecturedemo.cats.model.Cats
import com.odai.architecturedemo.favourite.model.FavouriteCats
import com.odai.architecturedemo.favourite.model.FavouriteState
import rx.Observable

interface CatRepository {

    fun saveCats(cats: Cats)

    fun readCats(): Observable<Cats>

    fun readFavouriteCats(): Observable<FavouriteCats>

    fun saveFavouriteCats(cats: FavouriteCats)

    fun saveCatFavoriteStatus(it: Pair<Cat, FavouriteState>)

}
