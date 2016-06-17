package com.odai.architecturedemo.favourite.service

import com.odai.architecturedemo.api.CatApi
import com.odai.architecturedemo.cat.model.Cat
import com.odai.architecturedemo.cats.model.Cats
import com.odai.architecturedemo.cats.service.CatsFreshnessChecker
import com.odai.architecturedemo.event.Event
import com.odai.architecturedemo.event.Status
import com.odai.architecturedemo.favourite.model.ActionState
import com.odai.architecturedemo.favourite.model.FavouriteCats
import com.odai.architecturedemo.favourite.model.FavouriteState
import com.odai.architecturedemo.favourite.model.FavouriteStatus
import com.odai.architecturedemo.persistence.CatRepository
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import rx.Observable
import rx.observers.TestObserver
import rx.subjects.BehaviorSubject
import java.net.URI

class PersistedFavouriteCatsServiceTest {

    var catApiSubject: BehaviorSubject<Cats> = BehaviorSubject.create()
    var catRepoSubject: BehaviorSubject<FavouriteCats> = BehaviorSubject.create()

    var api: CatApi = Mockito.mock(CatApi::class.java)
    var repository = Mockito.mock(CatRepository::class.java)
    var freshnessChecker = Mockito.mock(CatsFreshnessChecker::class.java)

    var service: FavouriteCatsService = PersistedFavouriteCatsService(api, repository, freshnessChecker)

    @Before
    fun setUp() {
        setUpService()
        service = PersistedFavouriteCatsService(api, repository, freshnessChecker)
    }

    @Test
    fun given_TheRepoIsEmpty_on_GetFavouriteCats_it_ShouldReturnCatsFromTheAPI() {
        val cats = FavouriteCats(mapOf(
                Pair(Cat(42, "Foo", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED)),
                Pair(Cat(24, "Bar", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED))
        ))
        val testObserver = TestObserver<FavouriteCats>()
        catApiSubject.onNext(Cats(cats.favourites.keys.toList()))
        catApiSubject.onCompleted()
        catRepoSubject.onCompleted()

        service.getFavouriteCats().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(cats))
    }

    @Test
    fun given_TheRepoIsEmpty_on_GetFavouriteCats_it_ShouldSaveCatsFromTheAPIInTheRepo() {
        val cats = FavouriteCats(mapOf(
                Pair(Cat(42, "Foo", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED)),
                Pair(Cat(24, "Bar", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED))
        ))
        val testObserver = TestObserver<FavouriteCats>()
        catApiSubject.onNext(Cats(cats.favourites.keys.toList()))
        catApiSubject.onCompleted()
        catRepoSubject.onCompleted()

        service.getFavouriteCats().subscribe(testObserver)

        Mockito.verify(repository).saveFavouriteCats(cats)
    }

    @Test
    fun given_TheRepoHasCatsAndCatsAreFresh_on_GetFavouriteCats_it_ShouldReturnCatsFromTheRepo() {
        val remoteCats = FavouriteCats(mapOf(
                Pair(Cat(42, "Foo", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED)),
                Pair(Cat(24, "Bar", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED))
        ))
        val localCats = FavouriteCats(mapOf(
                Pair(Cat(24, "Bar", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED))
        ))
        val testObserver = TestObserver<FavouriteCats>()
        catApiSubject.onNext(Cats(remoteCats.favourites.keys.toList()))
        catApiSubject.onCompleted()
        catRepoSubject.onNext(localCats)
        catRepoSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localCats)).thenReturn(true)

        service.getFavouriteCats().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(localCats))
    }

    @Test
    fun given_TheRepoHasCatsAndCatsAreExpired_on_GetFavouriteCats_it_ShouldReturnCatsFromTheRepoThenCatsFromTheAPI() {
        val remoteCats = FavouriteCats(mapOf(
                Pair(Cat(42, "Foo", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED)),
                Pair(Cat(24, "Bar", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED))
        ))
        val localCats = FavouriteCats(mapOf(
                Pair(Cat(24, "Bar", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED))
        ))
        val testObserver = TestObserver<FavouriteCats>()
        catApiSubject.onNext(Cats(remoteCats.favourites.keys.toList()))
        catApiSubject.onCompleted()
        catRepoSubject.onNext(localCats)
        catRepoSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localCats)).thenReturn(false)

        service.getFavouriteCats().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(localCats, remoteCats))
    }

    @Test
    fun given_TheRepoHasCatsAndCatsAreExpired_on_GetFavouriteCats_it_ShouldSaveCatsFromTheAPIInTheRepo() {
        val remoteCats = FavouriteCats(mapOf(
                Pair(Cat(42, "Foo", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED)),
                Pair(Cat(24, "Bar", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED))
        ))
        val localCats = FavouriteCats(mapOf(
                Pair(Cat(24, "Bar", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED))
        ))
        val testObserver = TestObserver<FavouriteCats>()
        catApiSubject.onNext(Cats(remoteCats.favourites.keys.toList()))
        catApiSubject.onCompleted()
        catRepoSubject.onNext(localCats)
        catRepoSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localCats)).thenReturn(false)

        service.getFavouriteCats().subscribe(testObserver)

        Mockito.verify(repository).saveFavouriteCats(remoteCats)
    }

    @Test
    fun given_TheRepoHasCatsAndAPIFails_on_GetFavouriteCats_it_ShouldReturnCatsFromTheRepo() {
        val localCats = FavouriteCats(mapOf(
                Pair(Cat(24, "Bar", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED))
        ))
        val testObserver = TestObserver<FavouriteCats>()
        catApiSubject.onError(Throwable())
        catRepoSubject.onNext(localCats)
        catRepoSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localCats)).thenReturn(false)

        service.getFavouriteCats().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(localCats))
    }

    @Test
    fun given_TheRepoIsEmptyAndAPIFails_on_GetFavouriteCats_it_ShouldReturnNothing() {
        val testObserver = TestObserver<FavouriteCats>()
        catApiSubject.onError(Throwable())
        catRepoSubject.onCompleted()

        service.getFavouriteCats().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf())
    }

    @Test
    fun given_TheRepoIsEmptyAndAPIIsEmpty_on_GetFavouriteCats_it_ShouldReturnNothing() {
        val testObserver = TestObserver<FavouriteCats>()
        catApiSubject.onCompleted()
        catRepoSubject.onCompleted()

        service.getFavouriteCats().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf())
    }

    @Test
    fun given_TheRepoFailsAndAPIIsEmpty_on_GetFavouriteCats_it_ShouldReturnNothing() {
        val testObserver = TestObserver<FavouriteCats>()
        catApiSubject.onCompleted()
        catRepoSubject.onError(Throwable())

        service.getFavouriteCats().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf())
    }

    @Test
    fun given_TheRepoFailsAndAPIHasCats_on_GetFavouriteCats_it_ShouldReturnNothing() {
        val cats = FavouriteCats(mapOf(
                Pair(Cat(24, "Bar", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED))
        ))
        val testObserver = TestObserver<FavouriteCats>()
        catApiSubject.onNext(Cats(cats.favourites.keys.toList()))
        catApiSubject.onCompleted()
        catRepoSubject.onError(Throwable())

        service.getFavouriteCats().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf())
    }

    @Test
    fun given_TheRepoIsEmpty_on_GetFavouriteCatsEvents_it_ShouldReturnCatsFromTheAPI() {
        val cats = FavouriteCats(mapOf(
                Pair(Cat(24, "Bar", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED))
        ))
        val testObserver = TestObserver<Event<FavouriteCats>>()
        catApiSubject.onNext(Cats(cats.favourites.keys.toList()))
        catApiSubject.onCompleted()
        catRepoSubject.onCompleted()

        service.getFavouriteCatsEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event<FavouriteCats>(Status.LOADING, null, null),
                Event(Status.LOADING, cats, null),
                Event(Status.IDLE, cats, null)
        ))
    }

    @Test
    fun given_TheRepoHasCatsAndCatsAreFresh_on_GetFavouriteCatsEvents_it_ShouldReturnCatsFromTheRepo() {
        val remoteCats = FavouriteCats(mapOf(
                Pair(Cat(42, "Foo", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED)),
                Pair(Cat(24, "Bar", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED))
        ))
        val localCats = FavouriteCats(mapOf(
                Pair(Cat(24, "Bar", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED))
        ))
        val testObserver = TestObserver<Event<FavouriteCats>>()
        catApiSubject.onNext(Cats(remoteCats.favourites.keys.toList()))
        catApiSubject.onCompleted()
        catRepoSubject.onNext(localCats)
        catRepoSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localCats)).thenReturn(true)

        service.getFavouriteCatsEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event<FavouriteCats>(Status.LOADING, null, null),
                Event(Status.LOADING, localCats, null),
                Event(Status.IDLE, localCats, null)
        ))
    }

    @Test
    fun given_TheRepoHasCatsAndCatsAreExpired_on_GetFavouriteCatsEvents_it_ShouldReturnCatsFromTheRepoThenCatsFromTheAPI() {
        val remoteCats = FavouriteCats(mapOf(
                Pair(Cat(42, "Foo", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED)),
                Pair(Cat(24, "Bar", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED))
        ))
        val localCats = FavouriteCats(mapOf(
                Pair(Cat(24, "Bar", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED))
        ))
        val testObserver = TestObserver<Event<FavouriteCats>>()
        catApiSubject.onNext(Cats(remoteCats.favourites.keys.toList()))
        catApiSubject.onCompleted()
        catRepoSubject.onNext(localCats)
        catRepoSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localCats)).thenReturn(false)

        service.getFavouriteCatsEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event<FavouriteCats>(Status.LOADING, null, null),
                Event(Status.LOADING, localCats, null),
                Event(Status.LOADING, remoteCats, null),
                Event(Status.IDLE, remoteCats, null)
        ))
    }

    @Test
    fun given_TheRepoIsEmptyAndAPIFails_on_GetFavouriteCatsEvents_it_ShouldReturnError() {
        val testObserver = TestObserver<Event<FavouriteCats>>()
        val throwable = Throwable()
        catApiSubject.onError(throwable)
        catRepoSubject.onCompleted()

        service.getFavouriteCatsEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event<FavouriteCats>(Status.LOADING, null, null),
                Event<FavouriteCats>(Status.ERROR, null, throwable)
        ))
    }

    @Test
    fun given_TheRepoIsEmptyAndAPIIsEmpty_on_GetFavouriteCatsEvents_it_ShouldReturnEmpty() {
        val testObserver = TestObserver<Event<FavouriteCats>>()
        catApiSubject.onCompleted()
        catRepoSubject.onCompleted()

        service.getFavouriteCatsEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event<FavouriteCats>(Status.LOADING, null, null),
                Event<FavouriteCats>(Status.IDLE, null, null)
        ))
    }

    @Test
    fun given_TheRepoFailsAndAPIIsEmpty_on_GetFavouriteCatsEvents_it_ShouldReturnError() {
        val testObserver = TestObserver<Event<FavouriteCats>>()
        val throwable = Throwable()
        catApiSubject.onCompleted()
        catRepoSubject.onError(throwable)

        service.getFavouriteCatsEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event<FavouriteCats>(Status.LOADING, null, null),
                Event<FavouriteCats>(Status.ERROR, null, throwable)
        ))
    }

    @Test
    fun given_TheRepoFailsAndAPIHasData_on_GetFavouriteCatsEvents_it_ShouldReturnError() {
        val cats = FavouriteCats(mapOf(
                Pair(Cat(42, "Foo", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED)),
                Pair(Cat(24, "Bar", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED))
        ))
        val testObserver = TestObserver<Event<FavouriteCats>>()
        val throwable = Throwable()
        catApiSubject.onNext(Cats(cats.favourites.keys.toList()))
        catApiSubject.onCompleted()
        catRepoSubject.onError(throwable)

        service.getFavouriteCatsEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event<FavouriteCats>(Status.LOADING, null, null),
                Event<FavouriteCats>(Status.ERROR, null, throwable)
        ))
    }

    @Test
    fun given_TheRepoHasDataAndAPIFails_on_GetFavouriteCatsEvents_it_ShouldReturnErrorWithData() {
        val cats = FavouriteCats(mapOf(
                Pair(Cat(42, "Foo", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED)),
                Pair(Cat(24, "Bar", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED))
        ))
        val testObserver = TestObserver<Event<FavouriteCats>>()
        val throwable = Throwable()
        catApiSubject.onError(throwable)
        catRepoSubject.onNext(cats)
        catRepoSubject.onCompleted()

        service.getFavouriteCatsEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event<FavouriteCats>(Status.LOADING, null, null),
                Event<FavouriteCats>(Status.LOADING, cats, null),
                Event<FavouriteCats>(Status.ERROR, cats, throwable)
        ))
    }

    @Test
    fun given_TheServiceDeliveredData_on_AddToFavourite_it_ShouldAddTheCatToTheFavourite() {
        val cats = FavouriteCats(mapOf(
                Pair(Cat(42, "Foo", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED))
        ))
        val testObserver = TestObserver<FavouriteCats>()
        catRepoSubject.onNext(cats)
        catRepoSubject.onCompleted()
        `when`(freshnessChecker.isFresh(cats)).thenReturn(true)
        service.getFavouriteCats().subscribe(testObserver)
        `when`(api.addToFavourite(Cat(424, "New", URI.create("")))).then({
            Observable.just(it.arguments[0])
        })

        service.addToFavourite(Cat(424, "New", URI.create("")))

        testObserver.assertReceivedOnNext(listOf(
                FavouriteCats(mapOf(
                        Pair(Cat(42, "Foo", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED))
                )),
                FavouriteCats(mapOf(
                        Pair(Cat(42, "Foo", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED)),
                        Pair(Cat(424, "New", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.PENDING))
                )),
                FavouriteCats(mapOf(
                        Pair(Cat(42, "Foo", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED)),
                        Pair(Cat(424, "New", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED))
                ))
        ))
    }

    @Test
    fun given_TheFavouriteApiFails_on_AddToFavourite_it_ShouldRevertTheCatToUnfavorite() {
        val cats = FavouriteCats(mapOf(
                Pair(Cat(42, "Foo", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED))
        ))
        val testObserver = TestObserver<FavouriteCats>()
        catRepoSubject.onNext(cats)
        catRepoSubject.onCompleted()
        `when`(freshnessChecker.isFresh(cats)).thenReturn(true)
        service.getFavouriteCats().subscribe(testObserver)
        `when`(api.addToFavourite(Cat(424, "New", URI.create("")))).then({
            Observable.error<Cat>(Throwable())
        })

        service.addToFavourite(Cat(424, "New", URI.create("")))

        testObserver.assertReceivedOnNext(listOf(
                FavouriteCats(mapOf(
                        Pair(Cat(42, "Foo", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED))
                )),
                FavouriteCats(mapOf(
                        Pair(Cat(42, "Foo", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED)),
                        Pair(Cat(424, "New", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.PENDING))
                )),
                FavouriteCats(mapOf(
                        Pair(Cat(42, "Foo", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED)),
                        Pair(Cat(424, "New", URI.create("")), FavouriteState(FavouriteStatus.UN_FAVOURITE, ActionState.CONFIRMED))
                ))
        ))
    }

    @Test
    fun given_TheServiceDeliveredData_on_RemoveFromFavourite_it_ShouldRemoveTheCatFromTheFavourite() {
        val cats = FavouriteCats(mapOf(
                Pair(Cat(42, "Foo", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED)),
                Pair(Cat(24, "Bar", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED))
        ))
        val testObserver = TestObserver<FavouriteCats>()
        catRepoSubject.onNext(cats)
        catRepoSubject.onCompleted()
        `when`(freshnessChecker.isFresh(cats)).thenReturn(true)
        service.getFavouriteCats().subscribe(testObserver)
        `when`(api.removeFromFavourite(Cat(24, "Bar", URI.create("")))).then({
            Observable.just(it.arguments[0])
        })

        service.removeFromFavourite(Cat(24, "Bar", URI.create("")))

        testObserver.assertReceivedOnNext(listOf(
                FavouriteCats(mapOf(
                        Pair(Cat(42, "Foo", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED)),
                        Pair(Cat(24, "Bar", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED))
                )),
                FavouriteCats(mapOf(
                        Pair(Cat(42, "Foo", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED)),
                        Pair(Cat(24, "Bar", URI.create("")), FavouriteState(FavouriteStatus.UN_FAVOURITE, ActionState.PENDING))
                )),
                FavouriteCats(mapOf(
                        Pair(Cat(42, "Foo", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED)),
                        Pair(Cat(24, "Bar", URI.create("")), FavouriteState(FavouriteStatus.UN_FAVOURITE, ActionState.CONFIRMED))
                ))
        ))
    }

    @Test
    fun given_TheFavouriteApiFails_on_RemoveFromFavourite_it_ShouldRevertTheCatBackToFavourite() {
        val cats = FavouriteCats(mapOf(
                Pair(Cat(42, "Foo", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED)),
                Pair(Cat(24, "Bar", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED))
        ))
        val testObserver = TestObserver<FavouriteCats>()
        catRepoSubject.onNext(cats)
        catRepoSubject.onCompleted()
        `when`(freshnessChecker.isFresh(cats)).thenReturn(true)
        service.getFavouriteCats().subscribe(testObserver)
        `when`(api.removeFromFavourite(Cat(24, "Bar", URI.create("")))).then({
            Observable.error<Cat>(Throwable())
        })

        service.removeFromFavourite(Cat(24, "Bar", URI.create("")))

        testObserver.assertReceivedOnNext(listOf(
                FavouriteCats(mapOf(
                        Pair(Cat(42, "Foo", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED)),
                        Pair(Cat(24, "Bar", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED))
                )),
                FavouriteCats(mapOf(
                        Pair(Cat(42, "Foo", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED)),
                        Pair(Cat(24, "Bar", URI.create("")), FavouriteState(FavouriteStatus.UN_FAVOURITE, ActionState.PENDING))
                )),
                FavouriteCats(mapOf(
                        Pair(Cat(42, "Foo", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED)),
                        Pair(Cat(24, "Bar", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED))
                ))
        ))
    }

    @Test
    fun given_TheServiceDeliveredData_on_AddToFavourite_it_ShouldPersistTheCatStateToTheRepo() {
        val cats = FavouriteCats(mapOf(
                Pair(Cat(42, "Foo", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED))
        ))
        val testObserver = TestObserver<FavouriteCats>()
        catRepoSubject.onNext(cats)
        catRepoSubject.onCompleted()
        `when`(freshnessChecker.isFresh(cats)).thenReturn(true)
        service.getFavouriteCats().subscribe(testObserver)
        `when`(api.addToFavourite(Cat(424, "New", URI.create("")))).then({
            Observable.just(it.arguments[0])
        })

        service.addToFavourite(Cat(424, "New", URI.create("")))

        Mockito.verify(repository).saveCatFavoriteStatus(Pair(
                Cat(424, "New", URI.create("")),
                FavouriteState(FavouriteStatus.FAVOURITE, ActionState.PENDING)
        ))
        Mockito.verify(repository).saveCatFavoriteStatus(Pair(
                Cat(424, "New", URI.create("")),
                FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED)
        ))
    }

    @Test
    fun given_TheFavouriteApiFails_on_AddToFavourite_it_ShouldPersistTheCatStateToTheRepo() {
        val cats = FavouriteCats(mapOf(
                Pair(Cat(42, "Foo", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED))
        ))
        val testObserver = TestObserver<FavouriteCats>()
        catRepoSubject.onNext(cats)
        catRepoSubject.onCompleted()
        `when`(freshnessChecker.isFresh(cats)).thenReturn(true)
        service.getFavouriteCats().subscribe(testObserver)
        `when`(api.addToFavourite(Cat(424, "New", URI.create("")))).then({
            Observable.error<Cat>(Throwable())
        })

        service.addToFavourite(Cat(424, "New", URI.create("")))

        Mockito.verify(repository).saveCatFavoriteStatus(Pair(
                Cat(424, "New", URI.create("")),
                FavouriteState(FavouriteStatus.FAVOURITE, ActionState.PENDING)
        ))
        Mockito.verify(repository).saveCatFavoriteStatus(Pair(
                Cat(424, "New", URI.create("")),
                FavouriteState(FavouriteStatus.UN_FAVOURITE, ActionState.CONFIRMED)
        ))
    }

    @Test
    fun given_TheServiceDeliveredData_on_RemoveFromFavourite_it_ShouldPersistTheCatStateToTheRepo() {
        val cats = FavouriteCats(mapOf(
                Pair(Cat(42, "Foo", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED)),
                Pair(Cat(24, "Bar", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED))
        ))
        val testObserver = TestObserver<FavouriteCats>()
        catRepoSubject.onNext(cats)
        catRepoSubject.onCompleted()
        `when`(freshnessChecker.isFresh(cats)).thenReturn(true)
        service.getFavouriteCats().subscribe(testObserver)
        `when`(api.removeFromFavourite(Cat(24, "Bar", URI.create("")))).then({
            Observable.just(it.arguments[0])
        })

        service.removeFromFavourite(Cat(24, "Bar", URI.create("")))

        Mockito.verify(repository).saveCatFavoriteStatus(Pair(
                Cat(24, "Bar", URI.create("")),
                FavouriteState(FavouriteStatus.UN_FAVOURITE, ActionState.PENDING)
        ))
        Mockito.verify(repository).saveCatFavoriteStatus(Pair(
                Cat(24, "Bar", URI.create("")),
                FavouriteState(FavouriteStatus.UN_FAVOURITE, ActionState.CONFIRMED)
        ))
    }

    @Test
    fun given_TheFavouriteApiFails_on_RemoveFromFavourite_it_ShouldPersistTheCatStateToTheRepo() {
        val cats = FavouriteCats(mapOf(
                Pair(Cat(42, "Foo", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED)),
                Pair(Cat(24, "Bar", URI.create("")), FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED))
        ))
        val testObserver = TestObserver<FavouriteCats>()
        catRepoSubject.onNext(cats)
        catRepoSubject.onCompleted()
        `when`(freshnessChecker.isFresh(cats)).thenReturn(true)
        service.getFavouriteCats().subscribe(testObserver)
        `when`(api.removeFromFavourite(Cat(24, "Bar", URI.create("")))).then({
            Observable.error<Cat>(Throwable())
        })

        service.removeFromFavourite(Cat(24, "Bar", URI.create("")))


        Mockito.verify(repository).saveCatFavoriteStatus(Pair(
                Cat(24, "Bar", URI.create("")),
                FavouriteState(FavouriteStatus.UN_FAVOURITE, ActionState.PENDING)
        ))
        Mockito.verify(repository).saveCatFavoriteStatus(Pair(
                Cat(24, "Bar", URI.create("")),
                FavouriteState(FavouriteStatus.FAVOURITE, ActionState.CONFIRMED)
        ))
    }

    private fun setUpService() {
        catApiSubject = BehaviorSubject.create()
        catRepoSubject = BehaviorSubject.create()
        val catsApiReplay = catApiSubject.replay()
        val catsRepoReplay = catRepoSubject.replay()
        catsApiReplay.connect()
        catsRepoReplay.connect()
        `when`(api.getFavouriteCats()).thenReturn(catsApiReplay)
        `when`(repository.readFavouriteCats()).thenReturn(catsRepoReplay)
    }

}
