package com.odai.architecturedemo.favourite.service

import com.jakewharton.rxrelay.BehaviorRelay
import com.odai.architecturedemo.api.CatApi
import com.odai.architecturedemo.cat.model.Cat
import com.odai.architecturedemo.cats.model.Cats
import com.odai.architecturedemo.cats.service.CatsFreshnessChecker
import com.odai.architecturedemo.event.*
import com.odai.architecturedemo.favourite.model.ActionState
import com.odai.architecturedemo.favourite.model.FavouriteCats
import com.odai.architecturedemo.favourite.model.FavouriteState
import com.odai.architecturedemo.favourite.model.FavouriteStatus
import com.odai.architecturedemo.persistence.CatRepository
import rx.Observable
import rx.Observer

class PersistedFavouriteCatsService(
        private val api: CatApi,
        private val repository: CatRepository,
        private val freshnessChecker: CatsFreshnessChecker
) : FavouriteCatsService {

    private val favouriteCatsRelay: BehaviorRelay<Event<FavouriteCats>> = BehaviorRelay.create(Event<FavouriteCats>(Status.IDLE, null, null))

    override fun getFavouriteCatsEvents(): Observable<Event<FavouriteCats>> {
        return favouriteCatsRelay.asObservable()
                .startWith(initialiseSubject())
                .distinctUntilChanged()
    }

    override fun getFavouriteCats() = getFavouriteCatsEvents().compose(asData())

    private fun initialiseSubject(): Observable<Event<FavouriteCats>> {
        if (isInitialised(favouriteCatsRelay)) {
            return Observable.empty()
        }
        return repository.readFavouriteCats()
                .flatMap { updateFromRemoteIfOutdated(it) }
                .switchIfEmpty(fetchRemoteFavouriteCats())
                .compose(asEvent<FavouriteCats>())
                .doOnNext { favouriteCatsRelay.call(it) }
    }

    private fun updateFromRemoteIfOutdated(it: FavouriteCats): Observable<FavouriteCats>? {
        return if (freshnessChecker.isFresh(it)) {
            Observable.just(it)
        } else {
            fetchRemoteFavouriteCats().startWith(it)
        }
    }

    private fun fetchRemoteFavouriteCats(): Observable<FavouriteCats> {
        return api.getFavouriteCats()
                .map { asFavouriteCats(it) }
                .doOnNext { repository.saveFavouriteCats(it) }
    }

    private fun asFavouriteCats(it: Cats): FavouriteCats {
        return FavouriteCats(
                it.fold(mapOf<Cat, FavouriteState>()) { map, cat ->
                    map.plus(Pair(cat, FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED)))
                }
        )
    }

    override fun addToFavourite(cat: Cat) {
        api.addToFavourite(cat)
                .map { Pair(cat, FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED)) }
                .onErrorReturn { Pair(cat, FavouriteState(FavouriteStatus.UN_FAVOURITE, ActionState.CONFIRMED)) }
                .startWith(Pair(cat, FavouriteState(FavouriteStatus.FAVOURITE, ActionState.PENDING)))
                .doOnNext { repository.saveCatFavoriteStatus(it) }
                .subscribe(favouriteCatStateObserver)
    }

    override fun removeFromFavourite(cat: Cat) {
        api.removeFromFavourite(cat)
                .map { Pair(cat, FavouriteState(FavouriteStatus.UN_FAVOURITE, ActionState.CONFIRMED)) }
                .onErrorReturn { Pair(cat, FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED)) }
                .startWith(Pair(cat, FavouriteState(FavouriteStatus.UN_FAVOURITE, ActionState.PENDING)))
                .doOnNext { repository.saveCatFavoriteStatus(it) }
                .subscribe(favouriteCatStateObserver)
    }

    private val favouriteCatStateObserver = object : Observer<Pair<Cat, FavouriteState>> {

        override fun onNext(p0: Pair<Cat, FavouriteState>) {
            val value = favouriteCatsRelay.value
            val favouriteCats = value.data ?: FavouriteCats(mapOf())
            favouriteCatsRelay.call(Event(value.status, favouriteCats.put(p0), value.error))
        }

        override fun onError(p0: Throwable?) {
            throw UnsupportedOperationException("Error on favourite state pipeline. This should never happen", p0)
        }

        override fun onCompleted() {
            // We don't want to finish the subject after a single favourite action so we don't do anything here
        }

    }


}
