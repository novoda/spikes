package com.odai.architecturedemo.cats.service

import com.odai.architecturedemo.cats.service.CatsService
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class AsyncCatsService(private val catsService: CatsService) : CatsService {

    override fun refreshCats() {
        Observable.create<Unit> { catsService.refreshCats() }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    override fun getCatsEvents() = catsService.getCatsEvents()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    override fun getCats() = catsService.getCats()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

}
