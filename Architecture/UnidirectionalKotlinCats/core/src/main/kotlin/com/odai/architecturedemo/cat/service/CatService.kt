package com.odai.architecturedemo.cat.service

import com.odai.architecturedemo.cat.model.Cat
import com.odai.architecturedemo.event.Event
import rx.Observable

interface CatService {

    fun getCatEvents(id: Int): Observable<Event<Cat>>

    fun getCat(id: Int): Observable<Cat>

    fun refreshCat()

}
