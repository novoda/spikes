package com.odai.architecturedemo.favourite.service

import com.odai.architecturedemo.cat.model.Cat
import com.odai.architecturedemo.favourite.service.FavouriteCatsService
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class AsyncFavouriteCatsService(private val favouriteCatsService: FavouriteCatsService) : FavouriteCatsService {

    override fun getFavouriteCatsEvents() = favouriteCatsService.getFavouriteCatsEvents()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    override fun getFavouriteCats() = favouriteCatsService.getFavouriteCats()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    override fun addToFavourite(cat: Cat) {
        Observable.create<Unit> { favouriteCatsService.addToFavourite(cat) }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    override fun removeFromFavourite(cat: Cat) {
        Observable.create<Unit> { favouriteCatsService.removeFromFavourite(cat) }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

}
