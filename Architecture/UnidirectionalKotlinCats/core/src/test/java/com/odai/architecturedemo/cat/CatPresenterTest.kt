package com.odai.architecturedemo.cat

import com.odai.architecturedemo.cat.model.Cat
import com.odai.architecturedemo.cat.service.CatService
import com.odai.architecturedemo.cat.view.CatView
import com.odai.architecturedemo.event.Event
import com.odai.architecturedemo.event.Status
import com.odai.architecturedemo.loading.LoadingView
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.*
import rx.subjects.BehaviorSubject
import java.net.URI
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CatPresenterTest {

    var catSubject: BehaviorSubject<Cat> = BehaviorSubject.create()
    var catEventSubject: BehaviorSubject<Event<Cat>> = BehaviorSubject.create()
    var service: CatService = mock(CatService::class.java)

    var view: CatView = mock(CatView::class.java)
    var loadingView: LoadingView = mock(LoadingView::class.java)

    var presenter = CatPresenter(42, service, view, loadingView)

    @Before
    fun setUp() {
        setUpService()
        presenter = CatPresenter(42, service, view, loadingView)
    }

    @After
    fun tearDown() {
        reset(view, loadingView, service)
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfANewCat_it_ShouldPresentTheCatToTheView() {
        givenThePresenterIsPresenting()

        catSubject.onNext(Cat(42, "NewCat", URI.create("")))

        verify(view).display(Cat(42, "NewCat", URI.create("")))
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfALoadingEventWithNoData_it_ShouldPresentTheLoadingScreen() {
        givenThePresenterIsPresenting()

        catEventSubject.onNext(Event(Status.LOADING, null, null))

        verify(loadingView).showLoadingScreen()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfALoadingEventWithData_it_ShouldPresentTheLoadingIndicator() {
        givenThePresenterIsPresenting()

        catEventSubject.onNext(Event(Status.LOADING, Cat(42, "NewCat", URI.create("")), null))

        verify(loadingView).showLoadingIndicator()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfAnIdleEventWithNoData_it_ShouldPresentTheEmptyScreen() {
        givenThePresenterIsPresenting()

        catEventSubject.onNext(Event(Status.IDLE, null, null))

        verify(loadingView).showEmptyScreen()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfAnIdleEventWithData_it_ShouldPresentData() {
        givenThePresenterIsPresenting()

        catEventSubject.onNext(Event(Status.IDLE, Cat(42, "NewCat", URI.create("")), null))

        verify(loadingView).showData()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfAnErrorEventWithNoData_it_ShouldPresentTheErrorScreen() {
        givenThePresenterIsPresenting()

        catEventSubject.onNext(Event(Status.ERROR, null, null))

        verify(loadingView).showErrorScreen()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfAnErrorEventWithData_it_ShouldPresentTheErrorIndicator() {
        givenThePresenterIsPresenting()

        catEventSubject.onNext(Event(Status.ERROR, Cat(42, "NewCat", URI.create("")), null))

        verify(loadingView).showErrorIndicator()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_RetryClicked_it_ShouldRefreshTheService() {
        givenThePresenterIsPresenting()

        presenter.retryListener.onRetry()

        verify(service).refreshCat()
    }

    @Test
    fun given_ThePresenterStoppedPresenting_on_EmissionOfANewCat_it_ShouldNotPresentTheCatToTheView() {
        givenThePresenterStoppedPresenting()

        catSubject.onNext(Cat(42, "NewCat", URI.create("")))

        verifyZeroInteractions(view)
    }

    @Test
    fun given_ThePresenterStoppedPresenting_on_EmissionOfAnEvent_it_ShouldNotPresentToTheLoadingView() {
        givenThePresenterStoppedPresenting()

        catEventSubject.onNext(Event(Status.LOADING, null, null))

        verifyZeroInteractions(loadingView)
    }

    @Test
    fun given_ThePresenterIsNotPresenting_on_StartPresenting_it_ShouldAttachListenerToTheLoadingView() {
        givenThePresenterIsNotPresenting()

        presenter.startPresenting()

        verify(loadingView).attach(presenter.retryListener)
    }

    @Test
    fun given_ThePresenterIsNotPresenting_on_StartPresenting_it_ShouldSubscribeToTheCatStream() {
        givenThePresenterIsNotPresenting()

        presenter.startPresenting()

        assertTrue(catSubject.hasObservers())
    }

    @Test
    fun given_ThePresenterIsNotPresenting_on_StartPresenting_it_ShouldSubscribeToTheEventStream() {
        givenThePresenterIsNotPresenting()

        presenter.startPresenting()

        assertTrue(catEventSubject.hasObservers())
    }

    @Test
    fun given_ThePresenterIsPresenting_on_StopPresenting_it_ShouldUnsubscribeFromTheCatStream() {
        givenThePresenterIsPresenting()

        presenter.stopPresenting()

        assertFalse(catSubject.hasObservers())
    }

    @Test
    fun given_ThePresenterIsPresenting_on_StopPresenting_it_ShouldUnsubscribeFromTheEventStream() {
        givenThePresenterIsPresenting()

        presenter.stopPresenting()

        assertFalse(catEventSubject.hasObservers())
    }

    private fun givenThePresenterIsPresenting() {
        presenter.startPresenting()
    }

    private fun givenThePresenterIsNotPresenting() {
    }

    private fun givenThePresenterStoppedPresenting() {
        presenter.startPresenting()
        reset(loadingView)
        presenter.stopPresenting()
    }

    private fun setUpService() {
        catSubject = BehaviorSubject.create()
        catEventSubject = BehaviorSubject.create()
        `when`(service.getCat(Mockito.anyInt())).thenReturn(catSubject)
        `when`(service.getCatEvents(Mockito.anyInt())).thenReturn(catEventSubject)
    }
}


