package com.odai.architecturedemo.cat.service

import com.odai.architecturedemo.cat.model.Cat
import com.odai.architecturedemo.cat.service.CatService
import com.odai.architecturedemo.cat.service.PersistedCatService
import com.odai.architecturedemo.cats.model.Cats
import com.odai.architecturedemo.cats.service.CatsService
import com.odai.architecturedemo.event.Event
import com.odai.architecturedemo.event.Status
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.*
import rx.subjects.BehaviorSubject
import java.net.URI


class PersistedCatServiceTest {

    var catsEventSubject: BehaviorSubject<Event<Cats>> = BehaviorSubject.create()
    var catsService: CatsService = mock(CatsService::class.java)

    var service: CatService = PersistedCatService(catsService)

    @Before
    fun setUp() {
        setUpService()
    }

    @Test
    fun given_aService_on_refreshCats_it_ShouldCallRefreshCatsOnTheCatsService() {
        service.refreshCat()

        verify(catsService).refreshCats()
    }

    @Test
    fun given_aServiceWithCats_on_getCatWithId_it_ShouldCallReturnCatForId() {
        catsEventSubject.onNext(Event(Status.IDLE, Cats(listOf(Cat(42, "Foo", URI.create("")), Cat(24, "Bar", URI.create("")))), null))

        val cat = service.getCat(24).toBlocking().first()

        assertEquals(Cat(24, "Bar", URI.create("")), cat)
    }

    @Test
    fun given_aServiceWithCats_on_getCatEvents_it_ShouldCallReturnCatForId() {
        catsEventSubject.onNext(Event(Status.IDLE, Cats(listOf(Cat(42, "Foo", URI.create("")), Cat(24, "Bar", URI.create("")))), null))

        val catEvent = service.getCatEvents(42).toBlocking().first()

        assertEquals(Cat(42, "Foo", URI.create("")), catEvent.data)
    }

    @Test
    fun given_aServiceWithStatus_on_getCatEvents_it_ShouldCallReturnMatchingStatus() {
        catsEventSubject.onNext(Event(Status.IDLE, Cats(listOf(Cat(42, "Foo", URI.create("")), Cat(24, "Bar", URI.create("")))), null))

        val catEvent = service.getCatEvents(24).toBlocking().first()

        assertEquals(Status.IDLE, catEvent.status)
    }

    @Test
    fun given_aServiceWithError_on_getCatEvents_it_ShouldCallReturnMatchingError() {
        var error = Throwable("Failed")
        catsEventSubject.onNext(Event(Status.ERROR, Cats(listOf(Cat(42, "Foo", URI.create("")), Cat(24, "Bar", URI.create("")))), error))

        val catEvent = service.getCatEvents(24).toBlocking().first()

        assertEquals(Status.ERROR, catEvent.status)
        assertEquals(error, catEvent.error)
    }

    private fun setUpService() {
        catsEventSubject = BehaviorSubject.create()
        `when`(catsService.getCatsEvents()).thenReturn(catsEventSubject)
    }

}
