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
    fun `Given tasksEvent is idle and empty, On getStatisticsEvent, It should send an idle event with empty statistics`() {
        tasksEventSubject.onNext(Event.idle())
        val testObserver = TestObserver<Event<Statistics>>()

        service.getStatisticsEvent().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.idle<Statistics>().updateData(emptyStatistics())
        ))
    }

    @Test
    fun `Given tasksEvent is loading and empty, On getStatisticsEvent, It should send a loading event with empty statistics`() {
        tasksEventSubject.onNext(Event.loading())
        val testObserver = TestObserver<Event<Statistics>>()

        service.getStatisticsEvent().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.loading<Statistics>().updateData(emptyStatistics())
        ))
    }

    @Test
    fun `Given tasksEvent is an error and empty, On getStatisticsEvent, It should send an error event with empty statistics`() {
        tasksEventSubject.onNext(Event.error(SyncError()))
        val testObserver = TestObserver<Event<Statistics>>()

        service.getStatisticsEvent().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.error<Statistics>(SyncError()).updateData(emptyStatistics())
        ))
    }

    @Test
    fun `Given tasksEvent is idle and has tasks, On getStatisticsEvent, It should send an idle event with statisticsmatching tasks`() {
        tasksEventSubject.onNext(Event.idle<Tasks>().updateData(tasksWithSomeActiveAndSomeCompleted()))
        val testObserver = TestObserver<Event<Statistics>>()

        service.getStatisticsEvent().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.idle<Statistics>().updateData(statisticsWithSomeActiveAndSomeCompletedTasks())
        ))
    }

    @Test
    fun `Given tasksEvent is loading and has tasks, On getStatisticsEvent, It should send a loading event with statisticsmatching tasks`() {
        tasksEventSubject.onNext(Event.loading<Tasks>().updateData(tasksWithSomeActiveAndSomeCompleted()))
        val testObserver = TestObserver<Event<Statistics>>()

        service.getStatisticsEvent().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.loading<Statistics>().updateData(statisticsWithSomeActiveAndSomeCompletedTasks())
        ))
    }

    @Test
    fun `Given tasksEvent is an error and has tasks, On getStatisticsEvent, It should send an error event with statisticsmatching tasks`() {
        tasksEventSubject.onNext(Event.error<Tasks>(SyncError()).updateData(tasksWithSomeActiveAndSomeCompleted()))
        val testObserver = TestObserver<Event<Statistics>>()

        service.getStatisticsEvent().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.error<Statistics>(SyncError()).updateData(statisticsWithSomeActiveAndSomeCompletedTasks())
        ))
    }

    private fun setUpService() {
        tasksEventSubject = BehaviorSubject.create()
        val tasksEventReplay = tasksEventSubject.replay()
        tasksEventReplay.connect()
        Mockito.`when`(tasksService.getTasksEvent()).thenReturn(tasksEventReplay)
    }

    private fun emptyStatistics() = Statistics.builder().build()

    private fun statisticsWithSomeActiveAndSomeCompletedTasks() = Statistics.builder()
            .activeTasks(1)
            .completedTasks(3)
            .build()

    private fun tasksWithSomeActiveAndSomeCompleted() = Tasks.asSynced(listOf(
            Task.builder().id(Id.from("24")).title("Bar").isCompleted(true).build(),
            Task.builder().id(Id.from("42")).title("Foo").isCompleted(true).build(),
            Task.builder().id(Id.from("12")).title("Whizz").build(),
            Task.builder().id(Id.from("424")).title("New").isCompleted(true).build()
    ), 123L)

}
