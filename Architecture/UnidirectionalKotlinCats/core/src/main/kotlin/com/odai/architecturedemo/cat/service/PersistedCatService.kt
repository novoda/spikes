package com.odai.architecturedemo.cat.service

import com.odai.architecturedemo.cat.model.Cat
import com.odai.architecturedemo.cats.service.CatsService
import com.odai.architecturedemo.event.Event
import com.odai.architecturedemo.event.asData
import rx.Observable

class PersistedCatService(private val catsService: CatsService) : CatService {

    override fun refreshCat() {
        catsService.refreshCats()
    }

    override fun getCatEvents(id: Int): Observable<Event<Cat>> {
        return catsService.getCatsEvents().map {
            Event<Cat>(it.status, it?.data?.first { it.id == id }, it.error)
        }
    }

    override fun getCat(id: Int): Observable<Cat> {
        return getCatEvents(id).compose(asData())
    }

}
