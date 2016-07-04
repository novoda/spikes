package com.novoda.todoapp.statistics.service

import com.novoda.event.Event
import com.novoda.todoapp.statistics.data.model.Statistics
import com.novoda.todoapp.task.data.model.Id
import com.novoda.todoapp.task.data.model.Task
import com.novoda.todoapp.tasks.data.model.Tasks
import com.novoda.todoapp.tasks.service.SyncError
import com.novoda.todoapp.tasks.service.TasksService
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import rx.observers.TestObserver
import rx.subjects.BehaviorSubject

class TasksStatisticsServiceTest {

    var tasksEventSubject: BehaviorSubject<Event<Tasks>> = BehaviorSubject.create()

    var tasksService = Mockito.mock(TasksService::class.java)

    var service: StatisticsService = TasksStatisticsService(tasksService)

    @Before
    fun setUp() {
        setUpService()
        service = TasksStatisticsService(tasksService)
    }

    @Test
    fun given_TasksEventIsIdleAndEmpty_on_getStatisticsEvent_it_ShouldSendAnIdleEventWithEmptyStatistics() {
        tasksEventSubject.onNext(Event.idle())
        val testObserver = TestObserver<Event<Statistics>>()

        service.getStatisticsEvent().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.idle<Statistics>().updateData(emptyStatistics())
        ))
    }

    @Test
    fun given_TasksEventIsLoadingAndEmpty_on_getStatisticsEvent_it_ShouldSendALoadingEventWithEmptyStatistics() {
        tasksEventSubject.onNext(Event.loading())
        val testObserver = TestObserver<Event<Statistics>>()

        service.getStatisticsEvent().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.loading<Statistics>().updateData(emptyStatistics())
        ))
    }

    @Test
    fun given_TasksEventIsErrorAndEmpty_on_getStatisticsEvent_it_ShouldSendAnErrorEventWithEmptyStatistics() {
        tasksEventSubject.onNext(Event.error(SyncError()))
        val testObserver = TestObserver<Event<Statistics>>()

        service.getStatisticsEvent().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.error<Statistics>(SyncError()).updateData(emptyStatistics())
        ))
    }

    @Test
    fun given_TasksEventIsIdleAndHasTasks_on_getStatisticsEvent_it_ShouldSendAnIdleEventWithStatisticsMatchingTasks() {
        tasksEventSubject.onNext(Event.idle<Tasks>().updateData(sampleSomeCompletedSomeActiveTasks()))
        val testObserver = TestObserver<Event<Statistics>>()

        service.getStatisticsEvent().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.idle<Statistics>().updateData(sampleStatisticsSomeCompletedSomeActiveTasks())
        ))
    }

    @Test
    fun given_TasksEventIsLoadingAndHasTasks_on_getStatisticsEvent_it_ShouldSendALoadingEventWithStatisticsMatchingTasks() {
        tasksEventSubject.onNext(Event.loading<Tasks>().updateData(sampleSomeCompletedSomeActiveTasks()))
        val testObserver = TestObserver<Event<Statistics>>()

        service.getStatisticsEvent().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.loading<Statistics>().updateData(sampleStatisticsSomeCompletedSomeActiveTasks())
        ))
    }

    @Test
    fun given_TasksEventIsErrorAndHasTasks_on_getStatisticsEvent_it_ShouldSendAnErrorEventWithStatisticsMatchingTasks() {
        tasksEventSubject.onNext(Event.error<Tasks>(SyncError()).updateData(sampleSomeCompletedSomeActiveTasks()))
        val testObserver = TestObserver<Event<Statistics>>()

        service.getStatisticsEvent().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.error<Statistics>(SyncError()).updateData(sampleStatisticsSomeCompletedSomeActiveTasks())
        ))
    }

    private fun setUpService() {
        tasksEventSubject = BehaviorSubject.create()
        val tasksEventReplay = tasksEventSubject.replay()
        tasksEventReplay.connect()
        Mockito.`when`(tasksService.getTasksEvent()).thenReturn(tasksEventReplay)
    }

    private fun emptyStatistics() = Statistics.builder().build()

    private fun sampleStatisticsSomeCompletedSomeActiveTasks() = Statistics.builder()
            .activeTasks(1)
            .completedTasks(3)
            .build()

    private fun sampleSomeCompletedSomeActiveTasks() = Tasks.asSynced(listOf(
            Task.builder().id(Id.from("24")).title("Bar").isCompleted(true).build(),
            Task.builder().id(Id.from("42")).title("Foo").isCompleted(true).build(),
            Task.builder().id(Id.from("12")).title("Whizz").build(),
            Task.builder().id(Id.from("424")).title("New").isCompleted(true).build()
    ), 123L)

}
