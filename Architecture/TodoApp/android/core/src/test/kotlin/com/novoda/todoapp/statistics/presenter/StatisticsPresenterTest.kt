package com.novoda.todoapp.statistics.presenter

import com.novoda.event.Event
import com.novoda.todoapp.navigation.TopLevelMenuDisplayer
import com.novoda.todoapp.navigation.Navigator
import com.novoda.todoapp.statistics.data.model.Statistics
import com.novoda.todoapp.statistics.displayer.StatisticsDisplayer
import com.novoda.todoapp.statistics.service.StatisticsService
import com.novoda.todoapp.tasks.service.SyncError
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.any
import org.mockito.Mockito.never
import rx.subjects.BehaviorSubject

class StatisticsPresenterTest {

    var statisticsEventSubject: BehaviorSubject<Event<Statistics>> = BehaviorSubject.create()

    var service: StatisticsService = Mockito.mock(StatisticsService::class.java)

    var displayer: StatisticsDisplayer = Mockito.mock(StatisticsDisplayer::class.java)

    var topLevelMenuDisplayer: TopLevelMenuDisplayer = Mockito.mock(TopLevelMenuDisplayer::class.java)

    var navigator: Navigator = Mockito.mock(Navigator::class.java)

    var presenter = StatisticsPresenter(service, displayer, topLevelMenuDisplayer, navigator)

    @Before
    fun setUp() {
        setUpService()
        presenter = StatisticsPresenter(service, displayer, topLevelMenuDisplayer, navigator)
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfNewIdleEventWithStatistics_it_ShouldPresentTheStatisticsToTheView() {
        givenThePresenterIsPresenting()

        statisticsEventSubject.onNext(Event.idle<Statistics>().updateData(statistics()))

        Mockito.verify(displayer).display(statistics())
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfNewLoadingEventWithStatistics_it_ShouldPresentTheStatisticsToTheView() {
        givenThePresenterIsPresenting()

        statisticsEventSubject.onNext(Event.loading<Statistics>().updateData(statistics()))

        Mockito.verify(displayer).display(statistics())
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfNewErrorEventWithStatistics_it_ShouldPresentTheStatisticsToTheView() {
        givenThePresenterIsPresenting()

        statisticsEventSubject.onNext(Event.error<Statistics>(SyncError()).updateData(statistics()))

        Mockito.verify(displayer).display(statistics())
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfNewIdleEventWithNoStatistics_it_ShouldNotPresentAnyStatisticsToTheView() {
        givenThePresenterIsPresenting()

        statisticsEventSubject.onNext(Event.idle<Statistics>())

        Mockito.verify(displayer, never()).display(any(Statistics::class.java))
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfNewLoadingEventWithNoStatistics_it_ShouldNotPresentAnyStatisticsToTheView() {
        givenThePresenterIsPresenting()

        statisticsEventSubject.onNext(Event.loading<Statistics>())

        Mockito.verify(displayer, never()).display(any(Statistics::class.java))
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfNewErrorEventWithNoStatistics_it_ShouldNotPresentAnyStatisticsToTheView() {
        givenThePresenterIsPresenting()

        statisticsEventSubject.onNext(Event.error<Statistics>(SyncError()))

        Mockito.verify(displayer, never()).display(any(Statistics::class.java))
    }

    @Test
    fun given_ThePresenterIsNotPresenting_on_EmissionOfNewIdleEventWithStatistics_it_ShouldNotPresentAnythingToTheView() {
        givenThePresenterIsNotPresenting()

        statisticsEventSubject.onNext(Event.idle<Statistics>().updateData(statistics()))

        Mockito.verifyNoMoreInteractions(displayer)
    }

    @Test
    fun given_ThePresenterIsNotPresenting_on_EmissionOfNewLoadingEventWithStatistics_it_ShouldNotPresentAnythingToTheView() {
        givenThePresenterIsNotPresenting()

        statisticsEventSubject.onNext(Event.loading<Statistics>().updateData(statistics()))

        Mockito.verifyNoMoreInteractions(displayer)
    }

    @Test
    fun given_ThePresenterIsNotPresenting_on_EmissionOfNewErrorEventWithStatistics_it_ShouldNotPresentAnythingToTheView() {
        givenThePresenterIsNotPresenting()

        statisticsEventSubject.onNext(Event.error<Statistics>(SyncError()).updateData(statistics()))

        Mockito.verifyNoMoreInteractions(displayer)
    }

    @Test
    fun given_ThePresenterIsNotPresenting_on_EmissionOfNewIdleEventWithNoStatistics_it_ShouldNotPresentAnythingToTheView() {
        givenThePresenterIsNotPresenting()

        statisticsEventSubject.onNext(Event.idle<Statistics>())

        Mockito.verifyNoMoreInteractions(displayer)
    }

    @Test
    fun given_ThePresenterIsNotPresenting_on_EmissionOfNewLoadingEventWithNoStatistics_it_ShouldNotPresentAnythingToTheView() {
        givenThePresenterIsNotPresenting()

        statisticsEventSubject.onNext(Event.loading<Statistics>())

        Mockito.verifyNoMoreInteractions(displayer)
    }

    @Test
    fun given_ThePresenterIsNotPresenting_on_EmissionOfNewErrorEventWithNoStatistics_it_ShouldNotPresentAnythingToTheView() {
        givenThePresenterIsNotPresenting()

        statisticsEventSubject.onNext(Event.error<Statistics>(SyncError()))

        Mockito.verifyNoMoreInteractions(displayer)
    }

    @Test
    fun given_ThePresenterIsNotPresenting_on_StartPresenting_it_ShouldAttachTheNavDrawerActionListener_ToTheDisplayer() {
        givenThePresenterIsNotPresenting()

        presenter.startPresenting()

        Mockito.verify(topLevelMenuDisplayer).attach(presenter.topLevelMenuActionListener)
    }

    @Test
    fun given_ThePresenterIsPresenting_on_StopPresenting_it_ShouldDetachTheNavDrawerActionListener_FromTheDisplayer() {
        givenThePresenterIsNotPresenting()

        presenter.stopPresenting()

        Mockito.verify(topLevelMenuDisplayer).detach()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_StatisticsNavDrawerItemSelected_it_ShouldCloseTheNavDrawer() {
        givenThePresenterIsPresenting()

        presenter.topLevelMenuActionListener.onStatisticsItemSelected()

        Mockito.verify(topLevelMenuDisplayer).closeMenu()
        Mockito.verifyZeroInteractions(navigator)
    }

    @Test
    fun given_ThePresenterIsPresenting_on_ToDoListNavDrawerItemSelected_it_ShouldCloseTheNavDrawerAndNavigateToTasksList() {
        givenThePresenterIsPresenting()

        presenter.topLevelMenuActionListener.onToDoListItemSelected()

        Mockito.verify(topLevelMenuDisplayer).closeMenu()
        Mockito.verify(navigator).toTasksList()
    }

    private fun givenThePresenterIsPresenting() {
        presenter.startPresenting()
    }

    private fun givenThePresenterIsNotPresenting() {
    }

    private fun statistics() = Statistics.builder().activeTasks(1).completedTasks(3).build()

    private fun setUpService() {
        statisticsEventSubject = BehaviorSubject.create()
        Mockito.`when`(service.getStatisticsEvent()).thenReturn(statisticsEventSubject)
    }

}
