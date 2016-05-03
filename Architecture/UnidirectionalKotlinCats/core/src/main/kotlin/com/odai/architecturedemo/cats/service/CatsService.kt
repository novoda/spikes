package com.odai.architecturedemo.cats.service

import com.odai.architecturedemo.cats.model.Cats
import com.odai.architecturedemo.event.Event
import rx.Observable


interface CatsService {

    fun getCatsEvents(): Observable<Event<Cats>>

    fun getCats(): Observable<Cats>

    fun refreshCats()

}
