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
    fun `Given the presenter is presenting, On emission of a new idle event with statistics, It should present the statistics to the displayer`() {
        givenThePresenterIsPresenting()

        statisticsEventSubject.onNext(Event.idle<Statistics>().updateData(statistics()))

        Mockito.verify(displayer).display(statistics())
    }

    @Test
    fun `Given the presenter is presenting, On emission of a new loading event with statistics, It should present the statistics to the displayer`() {
        givenThePresenterIsPresenting()

        statisticsEventSubject.onNext(Event.loading<Statistics>().updateData(statistics()))

        Mockito.verify(displayer).display(statistics())
    }

    @Test
    fun `Given the presenter is presenting, On emission of a new error event with statistics, It should present the statistics to the displayer`() {
        givenThePresenterIsPresenting()

        statisticsEventSubject.onNext(Event.error<Statistics>(SyncError()).updateData(statistics()))

        Mockito.verify(displayer).display(statistics())
    }

    @Test
    fun `Given the presenter is presenting, On emission of a new empty idle event, It should not present any statistics to the displayer`() {
        givenThePresenterIsPresenting()

        statisticsEventSubject.onNext(Event.idle<Statistics>())

        Mockito.verify(displayer, never()).display(any(Statistics::class.java))
    }

    @Test
    fun `Given the presenter is presenting, On emission of a new empty loading event, It should not present any statistics to the displayer`() {
        givenThePresenterIsPresenting()

        statisticsEventSubject.onNext(Event.loading<Statistics>())

        Mockito.verify(displayer, never()).display(any(Statistics::class.java))
    }

    @Test
    fun `Given the presenter is presenting, On emission of a new error loading event, It should not present any statistics to the displayer`() {
        givenThePresenterIsPresenting()

        statisticsEventSubject.onNext(Event.error<Statistics>(SyncError()))

        Mockito.verify(displayer, never()).display(any(Statistics::class.java))
    }

    @Test
    fun `Given the presenter is not presenting, On emission of a new idle event with statistics, It should not present anything to the displayer`() {
        givenThePresenterIsNotPresenting()

        statisticsEventSubject.onNext(Event.idle<Statistics>().updateData(statistics()))

        Mockito.verifyNoMoreInteractions(displayer)
    }

    @Test
    fun `Given the presenter is not presenting, On emission of a new loading event with statistics, It should not present anything to the displayer`() {
        givenThePresenterIsNotPresenting()

        statisticsEventSubject.onNext(Event.loading<Statistics>().updateData(statistics()))

        Mockito.verifyNoMoreInteractions(displayer)
    }

    @Test
    fun `Given the presenter is not presenting, On emission of a new error event with statistics, It should not present anything to the displayer`() {
        givenThePresenterIsNotPresenting()

        statisticsEventSubject.onNext(Event.error<Statistics>(SyncError()).updateData(statistics()))

        Mockito.verifyNoMoreInteractions(displayer)
    }

    @Test
    fun `Given the presenter is not presenting, On emission of a new empty idle event, It should not present anything to the displayer`() {
        givenThePresenterIsNotPresenting()

        statisticsEventSubject.onNext(Event.idle<Statistics>())

        Mockito.verifyNoMoreInteractions(displayer)
    }

    @Test
    fun `Given the presenter is not presenting, On emission of a new empty loading event, It should not present anything to the displayer`() {
        givenThePresenterIsNotPresenting()

        statisticsEventSubject.onNext(Event.loading<Statistics>())

        Mockito.verifyNoMoreInteractions(displayer)
    }

    @Test
    fun `Given the presenter is not presenting, On emission of a new empty error loading event, It should not present anything to the displayer`() {
        givenThePresenterIsNotPresenting()

        statisticsEventSubject.onNext(Event.error<Statistics>(SyncError()))

        Mockito.verifyNoMoreInteractions(displayer)
    }

    @Test
    fun `Given the presenter is not presenting, On start presenting, It should attach the action listener to the topLevelMenuDisplayer`() {
        givenThePresenterIsNotPresenting()

        presenter.startPresenting()

        Mockito.verify(topLevelMenuDisplayer).attach(presenter.topLevelMenuActionListener)
    }

    @Test
    fun `Given the presenter is presenting, On stop presenting, It should detach the action listener to the topLevelMenuDisplayer`() {
        givenThePresenterIsPresenting()

        presenter.stopPresenting()

        Mockito.verify(topLevelMenuDisplayer).detach()
    }

    @Test
    fun `Given the presenter is presenting, On statistics item selected, It should close the top level menu`() {
        givenThePresenterIsPresenting()

        presenter.topLevelMenuActionListener.onStatisticsItemSelected()

        Mockito.verify(topLevelMenuDisplayer).closeMenu()
        Mockito.verifyZeroInteractions(navigator)
    }

    @Test
    fun `Given the presenter is presenting, On todo list item selected, It should close the top level menu and navigate to the tasks list`() {
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
