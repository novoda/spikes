package com.novoda.todoapp.statistics.presenter

import com.novoda.event.Event
import com.novoda.spektacle.Spektacle
import com.novoda.todoapp.navigation.Navigator
import com.novoda.todoapp.navigation.TopLevelMenuDisplayer
import com.novoda.todoapp.statistics.data.model.Statistics
import com.novoda.todoapp.statistics.displayer.StatisticsDisplayer
import com.novoda.todoapp.statistics.service.StatisticsService
import com.novoda.todoapp.tasks.service.SyncError
import org.mockito.Mockito
import org.mockito.Mockito.never
import org.mockito.Mockito.reset
import rx.subjects.BehaviorSubject

class StatisticsPresenterTest: Spektacle<StatisticsPresenterTest.TestState>( {

    fun statistics(active: Int, completed: Int) = Statistics.builder().activeTasks(active).completedTasks(completed).build()!!
    fun statistics() = statistics(1, 3)

    given("the presenter is presenting") {
        definedAs {
            val testState = TestState()
            Mockito.`when`(testState.service.getStatisticsEvent()).thenReturn(testState.statisticsEventSubject)
            testState.presenter.startPresenting()
            return@definedAs testState
        }

        on("emission of a new idle event with statistics") {
            definedAs {
                resetMocks(it!!)
                it.statisticsEventSubject.onNext(Event.idle<Statistics>().updateData(statistics()))
                return@definedAs it
            }
            it("should present the statistics to the displayer") {
                Mockito.verify(it!!.displayer).display(statistics())
            }
        }

        on ("emission of a new loading event with statistics") {
            definedAs {
                resetMocks(it!!)
                it.statisticsEventSubject.onNext(Event.loading<Statistics>().updateData(statistics(4,2)))
                return@definedAs it
            }
            it("should present the statistics to the displayer") {
                Mockito.verify(it!!.displayer).display(statistics(4, 2))
            }
        }

        on ("emission of a new error event with statistics") {
            definedAs {
                resetMocks(it!!)
                it.statisticsEventSubject.onNext(Event.error<Statistics>(SyncError()).updateData(statistics(4, 3)))
                return@definedAs it
            }
            it("should present the statistics to the displayer") {
                Mockito.verify(it!!.displayer).display(statistics(4, 3))
            }
        }

        on ("emission of a new empty idle event") {
            definedAs {
                resetMocks(it!!)
                it.statisticsEventSubject.onNext(Event.idle<Statistics>())
                return@definedAs it
            }
            it("should not present any statistics to the displayer") {
                Mockito.verify(it!!.displayer, never()).display(statistics())
            }
        }

        on ("emission of a new empty loading event") {
            definedAs {
                resetMocks(it!!)
                it.statisticsEventSubject.onNext(Event.loading<Statistics>())
                return@definedAs it
            }
            it("should not present any statistics to the displayer") {
                Mockito.verify(it!!.displayer, never()).display(statistics())
            }
        }

        on ("emission of a new empty error event") {
            definedAs {
                resetMocks(it!!)
                it.statisticsEventSubject.onNext(Event.error<Statistics>(SyncError()))
                return@definedAs it
            }
            it("should not present any statistics to the displayer") {
                Mockito.verify(it!!.displayer, never()).display(statistics())
            }
        }

        on("statistics item selected") {
            definedAs {
                resetMocks(it!!)
                it.presenter.topLevelMenuActionListener.onStatisticsItemSelected()
                return@definedAs it
            }
            it("should close the top level menu") {
                Mockito.verify(it!!.topLevelMenuDisplayer).closeMenu()
                Mockito.verifyZeroInteractions(it.navigator)

            }
        }

        on ("todo list item selected") {
            definedAs {
                resetMocks(it!!)
                it.presenter.topLevelMenuActionListener.onToDoListItemSelected()
                return@definedAs it
            }
            it("should close the top level menu") {
                Mockito.verify(it!!.topLevelMenuDisplayer).closeMenu()
                Mockito.verify(it.navigator).toTasksList()

            }
        }

    }

    given ("the presenter is not presenting") {
        definedAs {
            val testState = TestState()
            Mockito.`when`(testState.service.getStatisticsEvent()).thenReturn(testState.statisticsEventSubject)
            return@definedAs testState
        }

        on ("emission of a new idle event with statistics") {
            definedAs {
                resetMocks(it!!)
                it.statisticsEventSubject.onNext(Event.idle<Statistics>().updateData(statistics()))
                return@definedAs it
            }
            it("should not present any statistics to the displayer") {
                Mockito.verify(it!!.displayer, never()).display(statistics())
            }
        }

        on("emission of a new loading event with statistics") {
            definedAs {
                resetMocks(it!!)
                it.statisticsEventSubject.onNext(Event.loading<Statistics>().updateData(statistics()))
                return@definedAs it
            }
            it("should not present any statistics to the displayer") {
                Mockito.verify(it!!.displayer, never()).display(statistics())
            }
        }

        on("emission of a new error event with statistics") {
            definedAs {
                resetMocks(it!!)
                it.statisticsEventSubject.onNext(Event.error<Statistics>(SyncError()).updateData(statistics()))
                return@definedAs it
            }
            it("should not present any statistics to the displayer") {
                Mockito.verify(it!!.displayer, never()).display(statistics())
            }
        }

        on("emission of a new empty idle event") {
            definedAs {
                resetMocks(it!!)
                it.statisticsEventSubject.onNext(Event.idle<Statistics>())
                return@definedAs it
            }
            it("should not present any statistics to the displayer") {
                Mockito.verify(it!!.displayer, never()).display(statistics())
            }
        }

        on("emission of a new empty loading event") {
            definedAs {
                resetMocks(it!!)
                it.statisticsEventSubject.onNext(Event.loading<Statistics>())
                return@definedAs it
            }
            it("should not present any statistics to the displayer") {
                Mockito.verify(it!!.displayer, never()).display(statistics())
            }
        }

        on("emission of a new empty error event") {
            definedAs {
                resetMocks(it!!)
                it.statisticsEventSubject.onNext(Event.error<Statistics>(SyncError()))
                return@definedAs it
            }
            it("should not present any statistics to the displayer") {
                Mockito.verify(it!!.displayer, never()).display(statistics())
            }
        }

    }

}) {
    class TestState {

        val statisticsEventSubject: BehaviorSubject<Event<Statistics>> = BehaviorSubject.create()
        val service: StatisticsService = Mockito.mock(StatisticsService::class.java)
        val displayer: StatisticsDisplayer = Mockito.mock(StatisticsDisplayer::class.java)
        val topLevelMenuDisplayer: TopLevelMenuDisplayer = Mockito.mock(TopLevelMenuDisplayer::class.java)
        val navigator: Navigator = Mockito.mock(Navigator::class.java)
        val presenter = StatisticsPresenter(service, displayer, topLevelMenuDisplayer, navigator)

    }

}

private fun resetMocks(it: StatisticsPresenterTest.TestState) {
    reset(it.displayer)
    reset(it.navigator)
    reset(it.topLevelMenuDisplayer)
}
