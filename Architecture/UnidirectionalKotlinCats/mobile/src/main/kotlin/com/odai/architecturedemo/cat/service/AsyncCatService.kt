package com.odai.architecturedemo.cat.service

import com.odai.architecturedemo.cat.service.CatService
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class AsyncCatService(private val catService: CatService) : CatService {

    override fun refreshCat() {
        Observable.create<Unit> { catService.refreshCat() }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    override fun getCatEvents(id: Int) = catService.getCatEvents(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    override fun getCat(id: Int) = catService.getCat(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

}
