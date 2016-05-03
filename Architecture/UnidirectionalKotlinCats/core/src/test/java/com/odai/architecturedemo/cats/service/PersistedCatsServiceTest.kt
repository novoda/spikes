package com.odai.architecturedemo.cats.service

import com.odai.architecturedemo.api.CatApi
import com.odai.architecturedemo.cat.model.Cat
import com.odai.architecturedemo.cats.model.Cats
import com.odai.architecturedemo.cats.service.CatsFreshnessChecker
import com.odai.architecturedemo.cats.service.CatsService
import com.odai.architecturedemo.cats.service.PersistedCatsService
import com.odai.architecturedemo.event.Event
import com.odai.architecturedemo.event.Status
import com.odai.architecturedemo.persistence.CatRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.*
import rx.observers.TestObserver
import rx.subjects.BehaviorSubject
import java.net.URI

class PersistedCatsServiceTest {

    var catApiSubject: BehaviorSubject<Cats> = BehaviorSubject.create()
    var catRepoSubject: BehaviorSubject<Cats> = BehaviorSubject.create()

    var api: CatApi = mock(CatApi::class.java)
    var repository = mock(CatRepository::class.java)
    var freshnessChecker = mock(CatsFreshnessChecker::class.java)

    var service: CatsService = PersistedCatsService(api, repository, freshnessChecker)

    @Before
    fun setUp() {
        setUpService()
        service = PersistedCatsService(api, repository, freshnessChecker)
    }

    @Test
    fun given_TheRepoIsEmpty_on_GetCats_it_ShouldReturnCatsFromTheAPI() {
        val cats = Cats(listOf(Cat(42, "Foo", URI.create("")), Cat(24, "Bar", URI.create(""))))
        val testObserver = TestObserver<Cats>()
        catApiSubject.onNext(cats)
        catApiSubject.onCompleted()
        catRepoSubject.onCompleted()

        service.getCats().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(cats))
    }

    @Test
    fun given_TheRepoIsEmpty_on_GetCats_it_ShouldSaveCatsFromTheAPIInTheRepo() {
        val cats = Cats(listOf(Cat(42, "Foo", URI.create("")), Cat(24, "Bar", URI.create(""))))
        val testObserver = TestObserver<Cats>()
        catApiSubject.onNext(cats)
        catApiSubject.onCompleted()
        catRepoSubject.onCompleted()

        service.getCats().subscribe(testObserver)

        verify(repository).saveCats(cats)
    }

    @Test
    fun given_TheRepoHasCatsAndCatsAreFresh_on_GetCats_it_ShouldReturnCatsFromTheRepo() {
        val remoteCats = Cats(listOf(Cat(42, "Foo", URI.create("")), Cat(24, "Bar", URI.create(""))))
        val localCats = Cats(listOf(Cat(24, "Bar", URI.create(""))))
        val testObserver = TestObserver<Cats>()
        catApiSubject.onNext(remoteCats)
        catApiSubject.onCompleted()
        catRepoSubject.onNext(localCats)
        catRepoSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localCats)).thenReturn(true)

        service.getCats().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(localCats))
    }

    @Test
    fun given_TheRepoHasCatsAndCatsAreExpired_on_GetCats_it_ShouldReturnCatsFromTheRepoThenCatsFromTheAPI() {
        val remoteCats = Cats(listOf(Cat(42, "Foo", URI.create("")), Cat(24, "Bar", URI.create(""))))
        val localCats = Cats(listOf(Cat(24, "Bar", URI.create(""))))
        val testObserver = TestObserver<Cats>()
        catApiSubject.onNext(remoteCats)
        catApiSubject.onCompleted()
        catRepoSubject.onNext(localCats)
        catRepoSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localCats)).thenReturn(false)

        service.getCats().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(localCats, remoteCats))
    }

    @Test
    fun given_TheRepoHasCatsAndCatsAreExpired_on_GetCats_it_ShouldSaveCatsFromTheAPIInTheRepo() {
        val remoteCats = Cats(listOf(Cat(42, "Foo", URI.create("")), Cat(24, "Bar", URI.create(""))))
        val localCats = Cats(listOf(Cat(24, "Bar", URI.create(""))))
        val testObserver = TestObserver<Cats>()
        catApiSubject.onNext(remoteCats)
        catApiSubject.onCompleted()
        catRepoSubject.onNext(localCats)
        catRepoSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localCats)).thenReturn(false)

        service.getCats().subscribe(testObserver)

        verify(repository).saveCats(remoteCats)
    }

    @Test
    fun given_TheRepoHasCatsAndAPIFails_on_GetCats_it_ShouldReturnCatsFromTheRepo() {
        val localCats = Cats(listOf(Cat(24, "Bar", URI.create(""))))
        val testObserver = TestObserver<Cats>()
        catApiSubject.onError(Throwable())
        catRepoSubject.onNext(localCats)
        catRepoSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localCats)).thenReturn(false)

        service.getCats().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(localCats))
    }

    @Test
    fun given_TheRepoIsEmptyAndAPIFails_on_GetCats_it_ShouldReturnNothing() {
        val testObserver = TestObserver<Cats>()
        catApiSubject.onError(Throwable())
        catRepoSubject.onCompleted()

        service.getCats().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf())
    }

    @Test
    fun given_TheRepoIsEmptyAndAPIIsEmpty_on_GetCats_it_ShouldReturnNothing() {
        val testObserver = TestObserver<Cats>()
        catApiSubject.onCompleted()
        catRepoSubject.onCompleted()

        service.getCats().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf())
    }

    @Test
    fun given_TheRepoFailsAndAPIIsEmpty_on_GetCats_it_ShouldReturnNothing() {
        val testObserver = TestObserver<Cats>()
        catApiSubject.onCompleted()
        catRepoSubject.onError(Throwable())

        service.getCats().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf())
    }

    @Test
    fun given_TheRepoFailsAndAPIHasCats_on_GetCats_it_ShouldReturnNothing() {
        val cats = Cats(listOf(Cat(42, "Foo", URI.create("")), Cat(24, "Bar", URI.create(""))))
        val testObserver = TestObserver<Cats>()
        catApiSubject.onNext(cats)
        catApiSubject.onCompleted()
        catRepoSubject.onError(Throwable())

        service.getCats().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf())
    }

    @Test
    fun given_TheRepoIsEmpty_on_GetCatsEvents_it_ShouldReturnCatsFromTheAPI() {
        val cats = Cats(listOf(Cat(42, "Foo", URI.create("")), Cat(24, "Bar", URI.create(""))))
        val testObserver = TestObserver<Event<Cats>>()
        catApiSubject.onNext(cats)
        catApiSubject.onCompleted()
        catRepoSubject.onCompleted()

        service.getCatsEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event<Cats>(Status.LOADING, null, null),
                Event(Status.LOADING, cats, null),
                Event(Status.IDLE, cats, null)
        ))
    }

    @Test
    fun given_TheRepoHasCatsAndCatsAreFresh_on_GetCatsEvents_it_ShouldReturnCatsFromTheRepo() {
        val remoteCats = Cats(listOf(Cat(42, "Foo", URI.create("")), Cat(24, "Bar", URI.create(""))))
        val localCats = Cats(listOf(Cat(24, "Bar", URI.create(""))))
        val testObserver = TestObserver<Event<Cats>>()
        catApiSubject.onNext(remoteCats)
        catApiSubject.onCompleted()
        catRepoSubject.onNext(localCats)
        catRepoSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localCats)).thenReturn(true)

        service.getCatsEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event<Cats>(Status.LOADING, null, null),
                Event(Status.LOADING, localCats, null),
                Event(Status.IDLE, localCats, null)
        ))
    }

    @Test
    fun given_TheRepoHasCatsAndCatsAreExpired_on_GetCatsEvents_it_ShouldReturnCatsFromTheRepoThenCatsFromTheAPI() {
        val remoteCats = Cats(listOf(Cat(42, "Foo", URI.create("")), Cat(24, "Bar", URI.create(""))))
        val localCats = Cats(listOf(Cat(24, "Bar", URI.create(""))))
        val testObserver = TestObserver<Event<Cats>>()
        catApiSubject.onNext(remoteCats)
        catApiSubject.onCompleted()
        catRepoSubject.onNext(localCats)
        catRepoSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localCats)).thenReturn(false)

        service.getCatsEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event<Cats>(Status.LOADING, null, null),
                Event(Status.LOADING, localCats, null),
                Event(Status.LOADING, remoteCats, null),
                Event(Status.IDLE, remoteCats, null)
        ))
    }

    @Test
    fun given_TheRepoIsEmptyAndAPIFails_on_GetCatsEvents_it_ShouldReturnError() {
        val testObserver = TestObserver<Event<Cats>>()
        val throwable = Throwable()
        catApiSubject.onError(throwable)
        catRepoSubject.onCompleted()

        service.getCatsEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event<Cats>(Status.LOADING, null, null),
                Event<Cats>(Status.ERROR, null, throwable)
        ))
    }

    @Test
    fun given_TheRepoIsEmptyAndAPIIsEmpty_on_GetCatsEvents_it_ShouldReturnEmpty() {
        val testObserver = TestObserver<Event<Cats>>()
        catApiSubject.onCompleted()
        catRepoSubject.onCompleted()

        service.getCatsEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event<Cats>(Status.LOADING, null, null),
                Event<Cats>(Status.IDLE, null, null)
        ))
    }

    @Test
    fun given_TheRepoFailsAndAPIIsEmpty_on_GetCatsEvents_it_ShouldReturnError() {
        val testObserver = TestObserver<Event<Cats>>()
        val throwable = Throwable()
        catApiSubject.onCompleted()
        catRepoSubject.onError(throwable)

        service.getCatsEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event<Cats>(Status.LOADING, null, null),
                Event<Cats>(Status.ERROR, null, throwable)
        ))
    }

    @Test
    fun given_TheRepoFailsAndAPIHasData_on_GetCatsEvents_it_ShouldReturnError() {
        val cats = Cats(listOf(Cat(42, "Foo", URI.create("")), Cat(24, "Bar", URI.create(""))))
        val testObserver = TestObserver<Event<Cats>>()
        val throwable = Throwable()
        catApiSubject.onNext(cats)
        catApiSubject.onCompleted()
        catRepoSubject.onError(throwable)

        service.getCatsEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event<Cats>(Status.LOADING, null, null),
                Event<Cats>(Status.ERROR, null, throwable)
        ))
    }

    @Test
    fun given_TheRepoHasDataAndAPIFails_on_GetCatsEvents_it_ShouldReturnErrorWithData() {
        val localCats = Cats(listOf(Cat(24, "Bar", URI.create(""))))
        val testObserver = TestObserver<Event<Cats>>()
        val throwable = Throwable()
        catApiSubject.onError(throwable)
        catRepoSubject.onNext(localCats)
        catRepoSubject.onCompleted()

        service.getCatsEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event<Cats>(Status.LOADING, null, null),
                Event<Cats>(Status.LOADING, localCats, null),
                Event<Cats>(Status.ERROR, localCats, throwable)
        ))
    }

    @Test
    fun given_apiDataHasChanged_on_RefreshCats_it_ShouldReturnNewData() {
        val cats = Cats(listOf(Cat(42, "Foo", URI.create("")), Cat(24, "Bar", URI.create(""))))
        val catsRefreshed = Cats(listOf(Cat(42, "Foo", URI.create("")), Cat(24, "Bar", URI.create("")), Cat(424, "New", URI.create(""))))
        val testObserver = TestObserver<Cats>()
        catApiSubject.onNext(cats)
        catApiSubject.onCompleted()
        catRepoSubject.onCompleted()
        service.getCats().subscribe(testObserver)
        setUpService()
        catApiSubject.onNext(catsRefreshed)
        catApiSubject.onCompleted()

        service.refreshCats()

        testObserver.assertReceivedOnNext(listOf(cats, catsRefreshed))
    }

    @Test
    fun given_apiDataHasNotChanged_on_RefreshCats_it_ShouldReturnNoAdditionalData() {
        val cats = Cats(listOf(Cat(42, "Foo", URI.create("")), Cat(24, "Bar", URI.create(""))))
        val testObserver = TestObserver<Cats>()
        catApiSubject.onNext(cats)
        catApiSubject.onCompleted()
        catRepoSubject.onCompleted()
        service.getCats().subscribe(testObserver)
        setUpService()
        catApiSubject.onNext(cats)
        catApiSubject.onCompleted()

        service.refreshCats()

        testObserver.assertReceivedOnNext(listOf(cats))
    }

    @Test
    fun given_apiDataHasChanged_on_RefreshCats_it_ShouldPersistNewDataToRepository() {
        val cats = Cats(listOf(Cat(42, "Foo", URI.create("")), Cat(24, "Bar", URI.create(""))))
        val catsRefreshed = Cats(listOf(Cat(42, "Foo", URI.create("")), Cat(24, "Bar", URI.create("")), Cat(424, "New", URI.create(""))))
        val testObserver = TestObserver<Cats>()
        catApiSubject.onNext(cats)
        catApiSubject.onCompleted()
        catRepoSubject.onCompleted()
        service.getCats().subscribe(testObserver)
        setUpService()
        catApiSubject.onNext(catsRefreshed)
        catApiSubject.onCompleted()

        service.refreshCats()

        verify(repository).saveCats(catsRefreshed)
    }

    @Test
    fun given_ServiceHasAlreadySentData_on_RefreshCats_it_ShouldRestartLoading() {
        val cats = Cats(listOf(Cat(42, "Foo", URI.create("")), Cat(24, "Bar", URI.create(""))))
        val catsRefreshed = Cats(listOf(Cat(42, "Foo", URI.create("")), Cat(24, "Bar", URI.create("")), Cat(424, "New", URI.create(""))))
        val testObserver = TestObserver<Event<Cats>>()
        catApiSubject.onNext(cats)
        catApiSubject.onCompleted()
        catRepoSubject.onCompleted()
        service.getCatsEvents().subscribe(testObserver)
        setUpService()
        catApiSubject.onNext(catsRefreshed)
        catApiSubject.onCompleted()

        service.refreshCats()

        testObserver.assertReceivedOnNext(listOf(
                Event<Cats>(Status.LOADING, null, null),
                Event<Cats>(Status.LOADING, cats, null),
                Event<Cats>(Status.IDLE, cats, null),
                Event<Cats>(Status.LOADING, null, null),
                Event<Cats>(Status.LOADING, catsRefreshed, null),
                Event<Cats>(Status.IDLE, catsRefreshed, null)
        ))
    }

    private fun setUpService() {
        catApiSubject = BehaviorSubject.create()
        catRepoSubject = BehaviorSubject.create()
        val catsApiReplay = catApiSubject.replay()
        val catsRepoReplay = catRepoSubject.replay()
        catsApiReplay.connect()
        catsRepoReplay.connect()
        `when`(api.getCats()).thenReturn(catsApiReplay)
        `when`(repository.readCats()).thenReturn(catsRepoReplay)
    }

}
