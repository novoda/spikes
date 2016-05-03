package com.odai.architecturedemo.cats

import com.odai.architecturedemo.cat.model.Cat
import com.odai.architecturedemo.cats.model.Cats
import com.odai.architecturedemo.cats.service.CatsService
import com.odai.architecturedemo.cats.view.CatsView
import com.odai.architecturedemo.event.Event
import com.odai.architecturedemo.event.Status
import com.odai.architecturedemo.favourite.model.ActionState
import com.odai.architecturedemo.favourite.model.FavouriteCats
import com.odai.architecturedemo.favourite.model.FavouriteState
import com.odai.architecturedemo.favourite.model.FavouriteStatus
import com.odai.architecturedemo.favourite.service.FavouriteCatsService
import com.odai.architecturedemo.loading.LoadingView
import com.odai.architecturedemo.navigation.Navigator
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import rx.subjects.BehaviorSubject
import java.net.URI
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CatsPresenterTest {

    var catsSubject: BehaviorSubject<Cats> = BehaviorSubject.create()
    var catsEventSubject: BehaviorSubject<Event<Cats>> = BehaviorSubject.create()
    var service: CatsService = mock(CatsService::class.java)

    var favouriteCatsSubject: BehaviorSubject<FavouriteCats> = BehaviorSubject.create()
    var favouriteService: FavouriteCatsService = mock(FavouriteCatsService::class.java)

    var view: CatsView = mock(CatsView::class.java)
    var loadingView: LoadingView = mock(LoadingView::class.java)

    var navigator: Navigator = mock(Navigator::class.java)

    var presenter = CatsPresenter(service, favouriteService, navigator, view, loadingView)

    @Before
    fun setUp() {
        setUpService()
        presenter = CatsPresenter(service, favouriteService, navigator, view, loadingView)
    }

    @After
    fun tearDown() {
        reset(view, loadingView, service, favouriteService)
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfNewCats_it_ShouldPresentTheCatsToTheView() {
        givenThePresenterIsPresenting()

        catsSubject.onNext(Cats(listOf(Cat(42, "NewCat", URI.create("")))))

        verify(view).display(Cats(listOf(Cat(42, "NewCat", URI.create("")))))
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfNewFavouritesCats_it_ShouldPresentTheCatsToTheView() {
        givenThePresenterIsPresenting()

        val favouriteCats = FavouriteCats(
                mapOf(Pair(Cat(42, "NewCat", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED)))
        )
        favouriteCatsSubject.onNext(favouriteCats)

        verify(view).display(favouriteCats)
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfALoadingEventWithNoData_it_ShouldPresentTheLoadingScreen() {
        givenThePresenterIsPresenting()

        catsEventSubject.onNext(Event(Status.LOADING, null, null))

        verify(loadingView).showLoadingScreen()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfALoadingEventWithData_it_ShouldPresentTheLoadingIndicator() {
        givenThePresenterIsPresenting()

        catsEventSubject.onNext(Event(Status.LOADING, Cats(listOf(Cat(42, "NewCat", URI.create("")))), null))

        verify(loadingView).showLoadingIndicator()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfAnIdleEventWithNoData_it_ShouldPresentTheEmptyScreen() {
        givenThePresenterIsPresenting()

        catsEventSubject.onNext(Event(Status.IDLE, null, null))

        verify(loadingView).showEmptyScreen()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfAnIdleEventWithData_it_ShouldPresentData() {
        givenThePresenterIsPresenting()

        catsEventSubject.onNext(Event(Status.IDLE, Cats(listOf(Cat(42, "NewCat", URI.create("")))), null))

        verify(loadingView).showData()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfAnErrorEventWithNoData_it_ShouldPresentTheErrorScreen() {
        givenThePresenterIsPresenting()

        catsEventSubject.onNext(Event(Status.ERROR, null, null))

        verify(loadingView).showErrorScreen()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfAnErrorEventWithData_it_ShouldPresentTheErrorIndicator() {
        givenThePresenterIsPresenting()

        catsEventSubject.onNext(Event(Status.ERROR, Cats(listOf(Cat(42, "NewCat", URI.create("")))), null))

        verify(loadingView).showErrorIndicator()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_RetryClicked_it_ShouldRefreshTheService() {
        givenThePresenterIsPresenting()

        presenter.retryListener.onRetry()

        verify(service).refreshCats()
    }

    @Test
    fun given_ThePresenterStoppedPresenting_on_EmissionOfANewCat_it_ShouldNotPresentTheCatToTheView() {
        givenThePresenterStoppedPresenting()

        catsSubject.onNext(Cats(listOf(Cat(42, "NewCat", URI.create("")))))

        verifyZeroInteractions(view)
    }

    @Test
    fun given_ThePresenterStoppedPresenting_on_EmissionOfNewFavouritesCats_it_ShouldNotPresentTheCatsToTheView() {
        givenThePresenterStoppedPresenting()

        val favouriteCats = FavouriteCats(
                mapOf(Pair(Cat(42, "NewCat", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED)))
        )
        favouriteCatsSubject.onNext(favouriteCats)

        verifyZeroInteractions(view)
    }

    @Test
    fun given_ThePresenterStoppedPresenting_on_EmissionOfAnEvent_it_ShouldNotPresentToTheLoadingView() {
        givenThePresenterStoppedPresenting()

        catsEventSubject.onNext(Event(Status.LOADING, null, null))

        verifyZeroInteractions(loadingView)
    }

    @Test
    fun given_ThePresenterIsNotPresenting_on_StartPresenting_it_ShouldAttachListenerToTheLoadingView() {
        givenThePresenterIsNotPresenting()

        presenter.startPresenting()

        verify(loadingView).attach(presenter.retryListener)
    }

    @Test
    fun given_ThePresenterIsNotPresenting_on_StartPresenting_it_ShouldAttachListenerToTheView() {
        givenThePresenterIsNotPresenting()

        presenter.startPresenting()

        verify(view).attach(presenter.catClickedListener)
    }

    @Test
    fun given_ThePresenterIsNotPresenting_on_StartPresenting_it_ShouldSubscribeToTheCatStream() {
        givenThePresenterIsNotPresenting()

        presenter.startPresenting()

        assertTrue(catsSubject.hasObservers())
    }

    @Test
    fun given_ThePresenterIsNotPresenting_on_StartPresenting_it_ShouldSubscribeToTheEventStream() {
        givenThePresenterIsNotPresenting()

        presenter.startPresenting()

        assertTrue(catsEventSubject.hasObservers())
    }

    @Test
    fun given_ThePresenterIsNotPresenting_on_StartPresenting_it_ShouldSubscribeToTheFavouriteCatStream() {
        givenThePresenterIsNotPresenting()

        presenter.startPresenting()

        assertTrue(favouriteCatsSubject.hasObservers())
    }

    @Test
    fun given_ThePresenterIsPresenting_on_StopPresenting_it_ShouldUnsubscribeFromTheCatStream() {
        givenThePresenterIsPresenting()

        presenter.stopPresenting()

        assertFalse(catsSubject.hasObservers())
    }

    @Test
    fun given_ThePresenterIsPresenting_on_StopPresenting_it_ShouldUnsubscribeFromTheEventStream() {
        givenThePresenterIsPresenting()

        presenter.stopPresenting()

        assertFalse(catsEventSubject.hasObservers())
    }

    @Test
    fun given_ThePresenterIsPresenting_on_StopPresenting_it_ShouldUnsubscribeFromTheFavouriteCatStream() {
        givenThePresenterIsPresenting()

        presenter.stopPresenting()

        assertFalse(favouriteCatsSubject.hasObservers())
    }

    @Test
    fun given_ThePresenterIsPresenting_on_CatClicked_it_ShouldNavigateToTheClickedCat() {
        givenThePresenterIsPresenting()

        presenter.catClickedListener.onCatClicked(Cat(42, "NewCat", URI.create("")))

        verify(navigator).toCat(Cat(42, "NewCat", URI.create("")))
    }

    @Test
    fun given_ThePresenterIsPresenting_on_FavouriteClickedForFavouriteCat_it_ShouldRemoveCatFromFavourites() {
        givenThePresenterIsPresenting()

        presenter.catClickedListener.onFavouriteClicked(
                Cat(42, "NewCat", URI.create("")),
                FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED)
        )

        verify(favouriteService).removeFromFavourite(Cat(42, "NewCat", URI.create("")))
    }

    @Test
    fun given_ThePresenterIsPresenting_on_FavouriteClickedForUnFavouriteCat_it_ShouldAddCatToFavourites() {
        givenThePresenterIsPresenting()

        presenter.catClickedListener.onFavouriteClicked(
                Cat(42, "NewCat", URI.create("")),
                FavouriteState(FavouriteStatus.UN_FAVOURITE, ActionState.CONFIRMED)
        )

        verify(favouriteService).addToFavourite(Cat(42, "NewCat", URI.create("")))
    }

    @Test
    fun given_ThePresenterIsPresenting_on_FavouriteClickedForPendingFavouriteCat_it_ShouldDoNothing() {
        givenThePresenterIsPresenting()

        presenter.catClickedListener.onFavouriteClicked(
                Cat(42, "NewCat", URI.create("")),
                FavouriteState(FavouriteStatus.FAVOURITE, ActionState.PENDING)
        )

        verifyZeroInteractions(favouriteService)
    }

    @Test
    fun given_ThePresenterIsPresenting_on_FavouriteClickedForPendingUnFavouriteCat_it_ShouldDoNothing() {
        givenThePresenterIsPresenting()

        presenter.catClickedListener.onFavouriteClicked(
                Cat(42, "NewCat", URI.create("")),
                FavouriteState(FavouriteStatus.UN_FAVOURITE, ActionState.PENDING)
        )

        verifyZeroInteractions(favouriteService)
    }

    private fun givenThePresenterIsPresenting() {
        presenter.startPresenting()
        reset(service, favouriteService)
    }

    private fun givenThePresenterIsNotPresenting() {
    }

    private fun givenThePresenterStoppedPresenting() {
        presenter.startPresenting()
        reset(loadingView, view)
        presenter.stopPresenting()
    }

    private fun setUpService() {
        catsSubject = BehaviorSubject.create()
        catsEventSubject = BehaviorSubject.create()
        favouriteCatsSubject = BehaviorSubject.create()
        `when`(service.getCats()).thenReturn(catsSubject)
        `when`(service.getCatsEvents()).thenReturn(catsEventSubject)
        `when`(favouriteService.getFavouriteCats()).thenReturn(favouriteCatsSubject)
    }
}


