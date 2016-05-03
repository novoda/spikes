package com.odai.architecturedemo.cats

import com.odai.architecturedemo.cat.model.Cat
import com.odai.architecturedemo.cats.model.Cats
import com.odai.architecturedemo.cats.service.CatsService
import com.odai.architecturedemo.cats.view.CatsView
import com.odai.architecturedemo.event.DataObserver
import com.odai.architecturedemo.event.Event
import com.odai.architecturedemo.event.EventObserver
import com.odai.architecturedemo.favourite.model.ActionState
import com.odai.architecturedemo.favourite.model.FavouriteCats
import com.odai.architecturedemo.favourite.model.FavouriteState
import com.odai.architecturedemo.favourite.model.FavouriteStatus
import com.odai.architecturedemo.favourite.service.FavouriteCatsService
import com.odai.architecturedemo.loading.LoadingView
import com.odai.architecturedemo.loading.RetryClickedListener
import com.odai.architecturedemo.navigation.Navigator
import rx.subscriptions.CompositeSubscription

class CatsPresenter(
        private val catsService: CatsService,
        private val favouriteCatsService: FavouriteCatsService,
        private val navigate: Navigator,
        private val catsView: CatsView,
        private val loadingView: LoadingView
) {

    private var subscriptions = CompositeSubscription()

    fun startPresenting() {
        catsView.attach(catClickedListener)
        loadingView.attach(retryListener)
        subscriptions.add(
                catsService.getCatsEvents()
                        .subscribe(catsEventsObserver)
        )
        subscriptions.add(
                catsService.getCats()
                        .subscribe(catsObserver)
        )
        subscriptions.add(
                favouriteCatsService.getFavouriteCats()
                        .subscribe(favouriteCatsObserver)
        )
    }

    fun stopPresenting() {
        subscriptions.clear()
        subscriptions = CompositeSubscription()
    }

    private val catsEventsObserver = object : EventObserver<Cats>() {
        override fun onLoading(event: Event<Cats>) {
            if (event.data != null) {
                loadingView.showLoadingIndicator()
            } else {
                loadingView.showLoadingScreen()
            }
        }

        override fun onIdle(event: Event<Cats>) {
            if (event.data != null) {
                loadingView.showData()
            } else {
                loadingView.showEmptyScreen()
            }
        }

        override fun onError(event: Event<Cats>) {
            if (event.data != null) {
                loadingView.showErrorIndicator()
            } else {
                loadingView.showErrorScreen()
            }
        }

    }

    private val catsObserver = object : DataObserver<Cats> {
        override fun onNext(p0: Cats) {
            catsView.display(p0);
        }
    }

    private val favouriteCatsObserver = object : DataObserver<FavouriteCats> {
        override fun onNext(p0: FavouriteCats) {
            catsView.display(p0)
        }
    }

    interface CatClickedListener {
        fun onFavouriteClicked(cat: Cat, state: FavouriteState)
        fun onCatClicked(cat: Cat)
    }

    val retryListener = object : RetryClickedListener {

        override fun onRetry() {
            catsService.refreshCats()
        }

    }

    val catClickedListener = object : CatClickedListener {

        override fun onCatClicked(cat: Cat) {
            navigate.toCat(cat)
        }

        override fun onFavouriteClicked(cat: Cat, state: FavouriteState) {
            if(state.state != ActionState.CONFIRMED) {
                return
            }
            if (state.status == FavouriteStatus.FAVOURITE) {
                favouriteCatsService.removeFromFavourite(cat)
            } else if (state.status == FavouriteStatus.UN_FAVOURITE) {
                favouriteCatsService.addToFavourite(cat)
            }
        }

    }

}
