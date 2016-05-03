package com.odai.architecturedemo.cats.service

import com.jakewharton.rxrelay.BehaviorRelay
import com.odai.architecturedemo.api.CatApi
import com.odai.architecturedemo.cats.model.Cats
import com.odai.architecturedemo.event.*
import com.odai.architecturedemo.persistence.CatRepository
import rx.Observable

class PersistedCatsService(
        private val api: CatApi,
        private val repository: CatRepository,
        private val catsFreshnessChecker: CatsFreshnessChecker
) : CatsService {

    private val catsRelay: BehaviorRelay<Event<Cats>> = BehaviorRelay.create(Event<Cats>(Status.IDLE, null, null))

    override fun getCatsEvents(): Observable<Event<Cats>> {
        return catsRelay.asObservable()
                .startWith(initialiseSubject())
                .distinctUntilChanged()
    }

    override fun getCats() = getCatsEvents().compose(asData())

    override fun refreshCats() {
        fetchRemoteCats()
                .compose(asEvent<Cats>())
                .subscribe { catsRelay.call(it) }
    }

    private fun initialiseSubject(): Observable<Event<Cats>> {
        if (isInitialised(catsRelay)) {
            return Observable.empty()
        }
        return repository.readCats()
                .flatMap { updateFromRemoteIfOutdated(it) }
                .switchIfEmpty(fetchRemoteCats())
                .compose(asEvent<Cats>())
                .doOnNext { catsRelay.call(it) }
    }

    private fun updateFromRemoteIfOutdated(it: Cats): Observable<Cats> {
        return if (catsFreshnessChecker.isFresh(it)) {
            Observable.just(it)
        } else {
            fetchRemoteCats().startWith(it)
        }
    }

    private fun fetchRemoteCats() = api.getCats()
            .doOnNext { repository.saveCats(it) }

}
