package com.odai.architecturedemo.persistence

import com.odai.architecturedemo.cat.model.Cat
import com.odai.architecturedemo.cats.model.Cats
import com.odai.architecturedemo.favourite.model.FavouriteCats
import com.odai.architecturedemo.favourite.model.FavouriteState
import rx.Observable
import rx.schedulers.Schedulers
import java.net.URI
import java.util.concurrent.TimeUnit

class InMemoryCatRepo : CatRepository {

    private var cats: Cats = Cats(listOf(
            Cat(404, "Not Found", URI.create("https://http.cat/404")),
            Cat(411, "Length Required", URI.create("https://http.cat/411")),
            Cat(418, "I'm a Teapot", URI.create("https://http.cat/418")),
            Cat(500, "Internal Server Error", URI.create("https://http.cat/500"))
    ))
    private var favouriteCats: FavouriteCats = FavouriteCats(mapOf())

    override fun saveCats(cats: Cats) {
        this.cats = cats
    }

    override fun readCats(): Observable<Cats> {
        if (cats.isEmpty()) {
            return Observable.empty()
        } else {
            return Observable.just(cats).delay(500, TimeUnit.MILLISECONDS, Schedulers.immediate())
        }
    }

    override fun readFavouriteCats(): Observable<FavouriteCats> {
        if (favouriteCats.isEmpty()) {
            return Observable.empty()
        } else {
            return Observable.just(favouriteCats).delay(500, TimeUnit.MILLISECONDS, Schedulers.immediate())
        }
    }

    override fun saveFavouriteCats(cats: FavouriteCats) {
        favouriteCats = cats
    }

    override fun saveCatFavoriteStatus(it: Pair<Cat, FavouriteState>) {
        favouriteCats = favouriteCats.put(it)
    }

}
