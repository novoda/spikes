package com.novoda.todoapp.tasks.service

import com.google.common.collect.ImmutableList
import com.novoda.data.SyncState
import com.novoda.data.SyncedData
import com.novoda.event.Event
import com.novoda.todoapp.task.data.model.Id
import com.novoda.todoapp.task.data.model.Task
import com.novoda.todoapp.tasks.NoEmptyTasksPredicate
import com.novoda.todoapp.tasks.data.TasksDataFreshnessChecker
import com.novoda.todoapp.tasks.data.model.Tasks
import com.novoda.todoapp.tasks.data.source.LocalTasksDataSource
import com.novoda.todoapp.tasks.data.source.RemoteTasksDataSource
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.*
import rx.Observable
import rx.observers.TestObserver
import rx.schedulers.Schedulers
import rx.schedulers.TestScheduler
import rx.subjects.BehaviorSubject
import kotlin.test.assertTrue

class PersistedTasksServiceTest {

    val TEST_TIME = 123L

    var taskRemoteDataSubject: BehaviorSubject<List<Task>> = BehaviorSubject.create()
    var taskLocalDataSubject: BehaviorSubject<Tasks> = BehaviorSubject.create()

    var remoteDataSource = Mockito.mock(RemoteTasksDataSource::class.java)
    var localDataSource = Mockito.mock(LocalTasksDataSource::class.java)
    var freshnessChecker = Mockito.mock(TasksDataFreshnessChecker::class.java)
    var clock = Mockito.mock(Clock::class.java)

    var service: TasksService = PersistedTasksService(localDataSource, remoteDataSource, freshnessChecker, clock, Schedulers.immediate())

    @Before
    fun setUp() {
        setUpService()
        service = PersistedTasksService(localDataSource, remoteDataSource, freshnessChecker, clock, Schedulers.immediate())
        Mockito.doAnswer {
            Observable.just(it.arguments[0])
        }.`when`(localDataSource).saveTasks(any())
        Mockito.doAnswer {
            Observable.just(it.arguments[0])
        }.`when`(localDataSource).saveTask(any())
        Mockito.doAnswer {
            Observable.just(it.arguments[0])
        }.`when`(remoteDataSource).saveTask(any())
        `when`(clock.timeInMillis()).thenReturn(TEST_TIME)
    }

    @Test
    fun `Given the local data has tasks and tasks are expired, On getTasksEvent, It should save tasks from the remote in the local data`() {
        val remoteTasks = sampleRemoteTasks()
        val localTasks = sampleLocalTasks()
        putTaskListIntoRemoteDataSource(remoteTasks)
        putTasksIntoLocalDataSource(localTasks)
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(false)

        subscribeToTasksEvent()

        verify(localDataSource).saveTasks(asSyncedTasks(remoteTasks))
    }


    @Test
    fun `Given the local data is empty, On getTasksEvent, It should return tasks from the remote`() {
        val testScheduler = givenPresenterWithReturnedScheduler()
        val tasks = sampleRemoteTasks()
        putTaskListIntoRemoteDataSource(tasks)
        taskLocalDataSubject.onCompleted()

        val testObserver = subscribeToTasksEvent()
        testScheduler.triggerActions()

        testObserver.assertReceivedOnNext(listOf(
                emptyIdleEvent(),
                emptyLoadingEvent(),
                loadingEventWith(asSyncedTasks(tasks)),
                idleEventWith(asSyncedTasks(tasks))
        ))
    }

    @Test
    fun `Given the local has tasks and tasks are fresh, On getTasksEvent, It should return tasks from the local data`() {
        val testScheduler = givenPresenterWithReturnedScheduler()
        val remoteTasks = sampleRemoteTasks()
        val localTasks = sampleLocalTasks()
        putTaskListIntoRemoteDataSource(remoteTasks)
        putTasksIntoLocalDataSource(localTasks)
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)

        val testObserver = subscribeToTasksEvent()
        testScheduler.triggerActions()

        testObserver.assertReceivedOnNext(listOf(
                emptyIdleEvent(),
                emptyLoadingEvent(),
                loadingEventWith(localTasks),
                idleEventWith(localTasks)
        ))
    }

    @Test
    fun `Given the local data has tasks and tasks are expired, On getTasksEvent, It should return tasks from the local data then tasks from the remote`() {
        val testScheduler = givenPresenterWithReturnedScheduler()
        val remoteTasks = sampleRemoteTasks()
        val localTasks = sampleLocalTasks()
        putTaskListIntoRemoteDataSource(remoteTasks)
        putTasksIntoLocalDataSource(localTasks)
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(false)

        val testObserver = subscribeToTasksEvent()
        testScheduler.triggerActions()

        testObserver.assertReceivedOnNext(listOf(
                emptyIdleEvent(),
                emptyLoadingEvent(),
                loadingEventWith(localTasks),
                loadingEventWith(asSyncedTasks(remoteTasks)),
                idleEventWith(asSyncedTasks(remoteTasks))
        ))
    }

    @Test
    fun `Given the local data is empty and remote fails, On getTasksEvent, It should return and error`() {
        val testScheduler = givenPresenterWithReturnedScheduler()
        val throwable = Throwable()
        taskRemoteDataSubject.onError(throwable)
        taskLocalDataSubject.onCompleted()

        val testObserver = subscribeToTasksEvent()
        testScheduler.triggerActions()

        testObserver.assertReceivedOnNext(listOf(
                emptyIdleEvent(),
                emptyLoadingEvent(),
                emptyErrorEvent(throwable)
        ))
    }

    @Test
    fun `Given the local data is empty and remote is empty, On getTasksEvent, It should return empty`() {
        val testScheduler = givenPresenterWithReturnedScheduler()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()

        val testObserver = subscribeToTasksEvent()
        testScheduler.triggerActions()

        testObserver.assertReceivedOnNext(listOf(
                emptyIdleEvent(),
                emptyLoadingEvent(),
                emptyIdleEvent()
        ))
    }

    @Test
    fun `Given the local data fails and remote is empty, On getTasksEvent, It should return an error`() {
        val testScheduler = givenPresenterWithReturnedScheduler()
        val throwable = Throwable()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onError(throwable)

        val testObserver = subscribeToTasksEvent()
        testScheduler.triggerActions()

        testObserver.assertReceivedOnNext(listOf(
                emptyIdleEvent(),
                emptyLoadingEvent(),
                emptyErrorEvent(throwable)
        ))
    }

    @Test
    fun `Given the local data fails and remote has data, On getTasksEvent, It should return an error`() {
        val testScheduler = givenPresenterWithReturnedScheduler()
        val tasks = sampleRemoteTasks()
        val throwable = Throwable()
        putTaskListIntoRemoteDataSource(tasks)
        taskLocalDataSubject.onError(throwable)

        val testObserver = subscribeToTasksEvent()
        testScheduler.triggerActions()

        testObserver.assertReceivedOnNext(listOf(
                emptyIdleEvent(),
                emptyLoadingEvent(),
                emptyErrorEvent(throwable)
        ))
    }

    @Test
    fun `Given the local data has data and remote fails, On getTasksEvent, It should return an error with data`() {
        val testScheduler = givenPresenterWithReturnedScheduler()
        val localTasks = sampleLocalTasks()
        val throwable = Throwable()
        taskRemoteDataSubject.onError(throwable)
        putTasksIntoLocalDataSource(localTasks)

        val testObserver = subscribeToTasksEvent()
        testScheduler.triggerActions()

        testObserver.assertReceivedOnNext(listOf(
                emptyIdleEvent(),
                emptyLoadingEvent(),
                loadingEventWith(localTasks),
                errorEventWith(localTasks, throwable)
        ))
    }

    @Test
    fun `Given the remote data source has changed, On refreshTasks, It should return new data`() {
        val tasks = sampleRemoteTasks()
        val tasksRefreshed = sampleRefreshedTasks()
        putTaskListIntoRemoteDataSource(tasks)
        taskLocalDataSubject.onCompleted()

        val testObserver = subscribeToTasksEvent()
        setUpService()
        putTaskListIntoRemoteDataSource(tasksRefreshed)

        service.refreshTasks()

        testObserver.assertReceivedOnNext(
                listOf(
                        idleEventWith(asSyncedTasks(tasks)),
                        loadingEventWith(asSyncedTasks(tasks)),
                        loadingEventWith(asSyncedTasks(tasksRefreshed)),
                        idleEventWith(asSyncedTasks(tasksRefreshed))
                )
        )
    }

    @Test
    fun `Given the remote data source has not changed, On refreshTasks, It should return no additional data`() {
        val tasks = sampleRemoteTasks()
        putTaskListIntoRemoteDataSource(tasks)
        taskLocalDataSubject.onCompleted()

        val testObserver = subscribeToTasksEvent()
        setUpService()
        putTaskListIntoRemoteDataSource(tasks)

        service.refreshTasks()

        testObserver.assertReceivedOnNext(
                listOf(
                        idleEventWith(asSyncedTasks(tasks)),
                        loadingEventWith(asSyncedTasks(tasks)),
                        idleEventWith(asSyncedTasks(tasks))
                )
        )
    }

    @Test
    fun `Given the remote data source has changed, On refreshTasks, It should persist the new data to the local data source`() {
        val tasks = sampleRemoteTasks()
        val tasksRefreshed = sampleRefreshedTasks()
        putTaskListIntoRemoteDataSource(tasks)
        taskLocalDataSubject.onCompleted()

        subscribeToTasksEvent()
        setUpService()
        putTaskListIntoRemoteDataSource(tasksRefreshed)

        service.refreshTasks()

        verify(localDataSource).saveTasks(asSyncedTasks(tasksRefreshed))
    }

    @Test
    fun `Given the service has already sent data, On refreshTasks, It should restart loading`() {
        val tasks = sampleRemoteTasks()
        val tasksRefreshed = sampleRefreshedTasks()
        putTaskListIntoRemoteDataSource(tasks)
        taskLocalDataSubject.onCompleted()

        val testObserver = subscribeToTasksEvent()
        setUpService()
        putTaskListIntoRemoteDataSource(tasksRefreshed)

        service.refreshTasks()

        testObserver.assertReceivedOnNext(listOf(
                idleEventWith(asSyncedTasks(tasks)),
                loadingEventWith(asSyncedTasks(tasks)),
                loadingEventWith(asSyncedTasks(tasksRefreshed)),
                idleEventWith(asSyncedTasks(tasksRefreshed))
        ))
    }

    @Test
    fun `Given remote is accepting updates, On completeTask, It should send AHEAD then IN_SYNC`() {
        val localTasks = sampleLocalTasks()
        val task = localTasks.all().get(0).data()
        putTasksIntoLocalDataSource(localTasks)
        taskRemoteDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321)
        val testObserver = subscribeToTasksEvent()

        service.complete(task)

        testObserver.assertReceivedOnNext(listOf(
                idleEventWith(localTasks),
                idleEventWith(localTasks.save(SyncedData.from(task.complete(), SyncState.AHEAD, 321))),
                idleEventWith(localTasks.save(SyncedData.from(task.complete(), SyncState.IN_SYNC, 321)))
        ))
        assertTrue(testObserver.onCompletedEvents.isEmpty(), "Service stream should never terminate")
    }

    @Test
    fun `Given the service has pending action more recent than current one, On completeTask, It should skip the updates for the current action`() {
        val (updatedTasks, updatedTask) = taskListWithOneAhead(sampleLocalTaskList(), 0)
        putTasksIntoLocalDataSource(updatedTasks)
        taskRemoteDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(updatedTasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321)

        val testObserver = subscribeToTasksEvent()

        service.complete(updatedTask)

        testObserver.assertReceivedOnNext(listOf(
                idleEventWith(updatedTasks)
        ))
        assertTrue(testObserver.onCompletedEvents.isEmpty(), "Service stream should never terminate")
    }

    @Test
    fun `Given remote is failing updates, On completeTask, It should send AHEAD then SYNC_ERROR`() {
        val localTasks = sampleLocalTasks()
        val task = localTasks.all().get(0).data()
        putTasksIntoLocalDataSource(localTasks)
        taskRemoteDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321)
        Mockito.doAnswer {
            Observable.error<Task>(Throwable())
        }.`when`(remoteDataSource).saveTask(any())

        val testObserver = subscribeToTasksEvent()

        service.complete(task)

        testObserver.assertReceivedOnNext(listOf(
                idleEventWith(localTasks),
                idleEventWith(localTasks.save(SyncedData.from(task.complete(), SyncState.AHEAD, 321))),
                idleEventWith(localTasks.save(SyncedData.from(task.complete(), SyncState.SYNC_ERROR, 321)))
        ))
        assertTrue(testObserver.onCompletedEvents.isEmpty(), "Service stream should never terminate")
    }

    @Test
    fun `Given remote is accepting updates, On activateTask, It should send AHEAD then IN_SYNC`() {
        val localTasks = sampleLocalCompletedTasks()
        val task = localTasks.all().get(0).data()
        putTasksIntoLocalDataSource(localTasks)
        taskRemoteDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321)

        val testObserver = subscribeToTasksEvent()

        service.activate(task)

        testObserver.assertReceivedOnNext(listOf(
                idleEventWith(localTasks),
                idleEventWith(localTasks.save(SyncedData.from(task.activate(), SyncState.AHEAD, 321))),
                idleEventWith(localTasks.save(SyncedData.from(task.activate(), SyncState.IN_SYNC, 321)))
        ))
        assertTrue(testObserver.onCompletedEvents.isEmpty(), "Service stream should never terminate")
    }

    @Test
    fun `Given the service has pending action more recent than current one, On activateTask, It should skip the updates for the current action`() {
        val (tasks, task) = taskListWithOneAhead(sampleLocalCompletedTaskList(), 0)
        taskRemoteDataSubject.onCompleted()
        putTasksIntoLocalDataSource(tasks)
        `when`(freshnessChecker.isFresh(tasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321)

        val testObserver = subscribeToTasksEvent()

        service.activate(task)

        testObserver.assertReceivedOnNext(listOf(
                idleEventWith(tasks)
        ))
        assertTrue(testObserver.onCompletedEvents.isEmpty(), "Service stream should never terminate")
    }

    @Test
    fun `Given remote is failing updates, On activateTask, It should send AHEAD then SYNC_ERROR`() {
        val localTasks = sampleLocalCompletedTasks()
        val task = localTasks.all().get(0).data()
        taskRemoteDataSubject.onCompleted()
        putTasksIntoLocalDataSource(localTasks)
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321)
        Mockito.doAnswer {
            Observable.error<Task>(Throwable())
        }.`when`(remoteDataSource).saveTask(any())

        val testObserver = subscribeToTasksEvent()

        service.activate(task)

        testObserver.assertReceivedOnNext(listOf(
                idleEventWith(localTasks),
                idleEventWith(localTasks.save(SyncedData.from(task.activate(), SyncState.AHEAD, 321))),
                idleEventWith(localTasks.save(SyncedData.from(task.activate(), SyncState.SYNC_ERROR, 321)))
        ))
        assertTrue(testObserver.onCompletedEvents.isEmpty(), "Service stream should never terminate")
    }

    @Test
    fun `Given remote is accepting updates, On saveTask, It should send AHEAD then IN_SYNC`() {
        val localTasks = sampleLocalCompletedTasks()
        val task = localTasks.all().get(0).data()
        putTasksIntoLocalDataSource(localTasks)
        taskRemoteDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321)

        val testObserver = subscribeToTasksEvent()

        val newTask = task.toBuilder().description("NewDesc").build()
        service.save(newTask)

        testObserver.assertReceivedOnNext(listOf(
                idleEventWith(localTasks),
                idleEventWith(localTasks.save(SyncedData.from(newTask, SyncState.AHEAD, 321))),
                idleEventWith(localTasks.save(SyncedData.from(newTask, SyncState.IN_SYNC, 321)))
        ))
        assertTrue(testObserver.onCompletedEvents.isEmpty(), "Service stream should never terminate")
    }

    @Test
    fun `Given the service has pending action more recent than current one, On saveTask, It should skip the updates for the current action`() {
        val (tasks, task) = taskListWithOneAhead(sampleLocalCompletedTaskList(), 0)
        taskRemoteDataSubject.onCompleted()
        putTasksIntoLocalDataSource(tasks)
        `when`(freshnessChecker.isFresh(tasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321)

        val testObserver = subscribeToTasksEvent()

        val newTask = task.toBuilder().description("NewDesc").build()
        service.save(newTask)

        testObserver.assertReceivedOnNext(listOf(
                idleEventWith(tasks)
        ))
        assertTrue(testObserver.onCompletedEvents.isEmpty(), "Service stream should never terminate")
    }

    @Test
    fun `Given remote is failing updates, On saveTask, It should send AHEAD then SYNC_ERROR`() {
        val localTasks = sampleLocalCompletedTasks()
        val task = localTasks.all().get(0).data()
        taskRemoteDataSubject.onCompleted()
        putTasksIntoLocalDataSource(localTasks)
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321)
        Mockito.doAnswer {
            Observable.error<Task>(Throwable())
        }.`when`(remoteDataSource).saveTask(any())

        val testObserver = subscribeToTasksEvent()

        val newTask = task.toBuilder().description("NewDesc").build()
        service.save(newTask)

        testObserver.assertReceivedOnNext(listOf(
                idleEventWith(localTasks),
                idleEventWith(localTasks.save(SyncedData.from(newTask, SyncState.AHEAD, 321))),
                idleEventWith(localTasks.save(SyncedData.from(newTask, SyncState.SYNC_ERROR, 321)))
        ))
        assertTrue(testObserver.onCompletedEvents.isEmpty(), "Service stream should never terminate")
    }

    @Test
    fun `Given the local data is empty, On getActiveTasksEvent, It should save tasks from the remote in the local data source`() {
        val tasks = sampleRemoteTasks()
        putTaskListIntoRemoteDataSource(tasks)
        taskLocalDataSubject.onCompleted()

        subscribeToActiveTasksEvent()

        verify(localDataSource).saveTasks(asSyncedTasks(tasks))
    }

    @Test
    fun `Given the local data has tasks and tasks are expired, On getActiveTasksEvent, It should save tasks from the remote in the local data source`() {
        val remoteTasks = sampleRemoteTasks()
        val localTasks = sampleLocalTasks()
        putTaskListIntoRemoteDataSource(remoteTasks)
        putTasksIntoLocalDataSource(localTasks)
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(false)

        subscribeToActiveTasksEvent()

        verify(localDataSource).saveTasks(asSyncedTasks(remoteTasks))
    }

    @Test
    fun `Given the local data is empty, On getActiveTasksEvent, It should return tasks from the remote`() {
        val testScheduler = givenPresenterWithReturnedScheduler()
        val tasks = sampleRemoteTasks()
        putTaskListIntoRemoteDataSource(tasks)
        taskLocalDataSubject.onCompleted()

        val testObserver = subscribeToActiveTasksEvent()
        testScheduler.triggerActions()

        testObserver.assertReceivedOnNext(listOf(
                emptyIdleEvent(),
                emptyLoadingEvent(),
                loadingEventWith(asSyncedTasks(tasks)),
                idleEventWith(asSyncedTasks(tasks))
        ))
    }

    @Test
    fun `Given the local data has tasks and tasks are fresh, On getActiveTasksEvent, It should return tasks from the local data`() {
        val testScheduler = givenPresenterWithReturnedScheduler()
        val remoteTasks = sampleRemoteTasks()
        val localTasks = sampleLocalTasks()
        putTaskListIntoRemoteDataSource(remoteTasks)
        putTasksIntoLocalDataSource(localTasks)
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)

        val testObserver = subscribeToActiveTasksEvent()
        testScheduler.triggerActions()

        testObserver.assertReceivedOnNext(listOf(
                emptyIdleEvent(),
                emptyLoadingEvent(),
                loadingEventWith(localTasks),
                idleEventWith(localTasks)
        ))
    }

    @Test
    fun `Given the local data has tasks and tasks are expired, On getActiveTasksEvent, It should return tasks from the local data then tasks from remote`() {
        val testScheduler = givenPresenterWithReturnedScheduler()
        val remoteTasks = sampleRemoteTasks()
        val localTasks = sampleLocalTasks()
        putTaskListIntoRemoteDataSource(remoteTasks)
        putTasksIntoLocalDataSource(localTasks)
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(false)

        val testObserver = subscribeToActiveTasksEvent()
        testScheduler.triggerActions()

        testObserver.assertReceivedOnNext(listOf(
                emptyIdleEvent(),
                emptyLoadingEvent(),
                loadingEventWith(localTasks),
                loadingEventWith(asSyncedTasks(remoteTasks)),
                idleEventWith(asSyncedTasks(remoteTasks))
        ))
    }

    @Test
    fun `Given the local data is empty and remote fails, On getActiveTasksEvent, It should return an error`() {
        val testScheduler = givenPresenterWithReturnedScheduler()
        val throwable = Throwable()
        taskRemoteDataSubject.onError(throwable)
        taskLocalDataSubject.onCompleted()

        val testObserver = subscribeToActiveTasksEvent()
        testScheduler.triggerActions()

        testObserver.assertReceivedOnNext(listOf(
                emptyIdleEvent(),
                emptyLoadingEvent(),
                emptyErrorEvent(throwable)
        ))
    }

    @Test
    fun `Given the local data is empty and remote is empty, On getActiveTasksEvent, It should return empty`() {
        val testScheduler = givenPresenterWithReturnedScheduler()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()

        val testObserver = subscribeToActiveTasksEvent()
        testScheduler.triggerActions()

        testObserver.assertReceivedOnNext(listOf(
                emptyIdleEvent(),
                emptyLoadingEvent(),
                emptyIdleEvent()
        ))
    }

    @Test
    fun `Given the local data fails and remote is empty, On getActiveTasksEvent, It should return an error`() {
        val testScheduler = givenPresenterWithReturnedScheduler()
        val throwable = Throwable()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onError(throwable)

        val testObserver = subscribeToActiveTasksEvent()
        testScheduler.triggerActions()

        testObserver.assertReceivedOnNext(listOf(
                emptyIdleEvent(),
                emptyLoadingEvent(),
                emptyErrorEvent(throwable)
        ))
    }

    @Test
    fun `Given the local data fails and remote has data, On getActiveTasksEvent, It should return an error`() {
        val testScheduler = givenPresenterWithReturnedScheduler()
        val tasks = sampleRemoteTasks()
        val throwable = Throwable()
        putTaskListIntoRemoteDataSource(tasks)
        taskLocalDataSubject.onError(throwable)

        val testObserver = subscribeToActiveTasksEvent()
        testScheduler.triggerActions()

        testObserver.assertReceivedOnNext(listOf(
                emptyIdleEvent(),
                emptyLoadingEvent(),
                emptyErrorEvent(throwable)
        ))
    }

    @Test
    fun `Given the local data has data and remote fails, On getActiveTasksEvent, It should return an error with data`() {
        val testScheduler = givenPresenterWithReturnedScheduler()
        val localTasks = sampleLocalTasks()
        val throwable = Throwable()
        taskRemoteDataSubject.onError(throwable)
        putTasksIntoLocalDataSource(localTasks)

        val testObserver = subscribeToActiveTasksEvent()
        testScheduler.triggerActions()

        testObserver.assertReceivedOnNext(listOf(
                emptyIdleEvent(),
                emptyLoadingEvent(),
                loadingEventWith(localTasks),
                errorEventWith(localTasks, throwable)
        ))
    }

    @Test
    fun `Given the tasks are all completed, On getActiveTasksEvent, It should return empty`() {
        val testScheduler = givenPresenterWithReturnedScheduler()
        val tasks = sampleLocalCompletedTasks()
        taskRemoteDataSubject.onCompleted()
        putTasksIntoLocalDataSource(tasks)

        val testObserver = subscribeToActiveTasksEvent()
        testScheduler.triggerActions()

        testObserver.assertReceivedOnNext(listOf(
                emptyIdleEvent(),
                emptyLoadingEvent(),
                emptyLoadingEvent(),
                emptyIdleEvent()
        ))
    }

    @Test
    fun `Given some tasks are completed, On getActiveTasksEvent, It should filter tasks`() {
        val testScheduler = givenPresenterWithReturnedScheduler()
        val tasks = sampleLocalSomeCompletedTasks()
        taskRemoteDataSubject.onCompleted()
        putTasksIntoLocalDataSource(tasks)

        val testObserver = subscribeToActiveTasksEvent()
        testScheduler.triggerActions()

        testObserver.assertReceivedOnNext(listOf(
                emptyIdleEvent(),
                emptyLoadingEvent(),
                loadingEventWith(sampleLocalActivatedTasks()),
                idleEventWith(sampleLocalActivatedTasks())
        ))
    }

    @Test
    fun `Given the local data is empty, On getCompletedTasksEvent, It should save tasks from the remote in the local data source`() {
        val tasks = sampleRemoteCompletedTasks()
        putTaskListIntoRemoteDataSource(tasks)
        taskLocalDataSubject.onCompleted()

        subscribeToCompletedTasksEvent()

        verify(localDataSource).saveTasks(asSyncedTasks(tasks))
    }

    @Test
    fun `Given the local data has tasks and tasks are expired, On getCompletedTasksEvent, It should save tasks from the remote in the local data source`() {
        val remoteTasks = sampleRemoteCompletedTasks()
        val localTasks = sampleLocalCompletedTasks()
        putTaskListIntoRemoteDataSource(remoteTasks)
        putTasksIntoLocalDataSource(localTasks)
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(false)

        subscribeToCompletedTasksEvent()

        verify(localDataSource).saveTasks(asSyncedTasks(remoteTasks))
    }

    @Test
    fun `Given the local data is empty, On getCompletedTasksEvent, It should return tasks from the remote`() {
        val testScheduler = givenPresenterWithReturnedScheduler()
        val tasks = sampleRemoteCompletedTasks()
        putTaskListIntoRemoteDataSource(tasks)
        taskLocalDataSubject.onCompleted()

        val testObserver = subscribeToCompletedTasksEvent()
        testScheduler.triggerActions()

        testObserver.assertReceivedOnNext(listOf(
                emptyIdleEvent(),
                emptyLoadingEvent(),
                loadingEventWith(asSyncedTasks(tasks)),
                idleEventWith(asSyncedTasks(tasks))
        ))
    }

    @Test
    fun `Given the local data has tasks and tasks are fresh, On getCompletedTasksEvent, It should return tasks from the local data`() {
        val testScheduler = givenPresenterWithReturnedScheduler()
        val remoteTasks = sampleRemoteCompletedTasks()
        val localTasks = sampleLocalCompletedTasks()
        putTaskListIntoRemoteDataSource(remoteTasks)
        putTasksIntoLocalDataSource(localTasks)
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)

        val testObserver = subscribeToCompletedTasksEvent()
        testScheduler.triggerActions()

        testObserver.assertReceivedOnNext(listOf(
                emptyIdleEvent(),
                emptyLoadingEvent(),
                loadingEventWith(localTasks),
                idleEventWith(localTasks)
        ))
    }

    @Test
    fun `Given the local data has tasks and tasks are expired, On getCompletedTasksEvent, It should return tasks from the local data then tasks from the remote`() {
        val testScheduler = givenPresenterWithReturnedScheduler()
        val remoteTasks = sampleRemoteCompletedTasks()
        val localTasks = sampleLocalCompletedTasks()
        putTaskListIntoRemoteDataSource(remoteTasks)
        putTasksIntoLocalDataSource(localTasks)
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(false)

        val testObserver = subscribeToCompletedTasksEvent()
        testScheduler.triggerActions()

        testObserver.assertReceivedOnNext(listOf(
                emptyIdleEvent(),
                emptyLoadingEvent(),
                loadingEventWith(localTasks),
                loadingEventWith(asSyncedTasks(remoteTasks)),
                idleEventWith(asSyncedTasks(remoteTasks))
        ))
    }

    @Test
    fun `Given the local data is empty and remote fails, On getCompletedTasksEvent, It should return an error`() {
        val testScheduler = givenPresenterWithReturnedScheduler()
        val throwable = Throwable()
        taskRemoteDataSubject.onError(throwable)
        taskLocalDataSubject.onCompleted()

        val testObserver = subscribeToCompletedTasksEvent()
        testScheduler.triggerActions()

        testObserver.assertReceivedOnNext(listOf(
                emptyIdleEvent(),
                emptyLoadingEvent(),
                emptyErrorEvent(throwable)
        ))
    }

    @Test
    fun `Given the local data is empty and remote is empty, On getCompletedTasksEvent, It should return empty`() {
        val testScheduler = givenPresenterWithReturnedScheduler()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()

        val testObserver = subscribeToCompletedTasksEvent()
        testScheduler.triggerActions()

        testObserver.assertReceivedOnNext(listOf(
                emptyIdleEvent(),
                emptyLoadingEvent(),
                emptyIdleEvent()
        ))
    }

    @Test
    fun `Given the local data fails and remote is empty, On getCompletedTasksEvent, It should return an error`() {
        val testScheduler = givenPresenterWithReturnedScheduler()
        val throwable = Throwable()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onError(throwable)

        val testObserver = subscribeToCompletedTasksEvent()
        testScheduler.triggerActions()

        testObserver.assertReceivedOnNext(listOf(
                emptyIdleEvent(),
                emptyLoadingEvent(),
                emptyErrorEvent(throwable)
        ))
    }

    @Test
    fun `Given the local data fails and remote has data, On getCompletedTasksEvent, It should return an error`() {
        val testScheduler = givenPresenterWithReturnedScheduler()
        val tasks = sampleRemoteCompletedTasks()
        val throwable = Throwable()
        putTaskListIntoRemoteDataSource(tasks)
        taskLocalDataSubject.onError(throwable)

        val testObserver = subscribeToCompletedTasksEvent()
        testScheduler.triggerActions()

        testObserver.assertReceivedOnNext(listOf(
                emptyIdleEvent(),
                emptyLoadingEvent(),
                emptyErrorEvent(throwable)
        ))
    }

    @Test
    fun `Given the local data has data and remote fails, On getCompletedTasksEvent, It should return an error with data`() {
        val testScheduler = givenPresenterWithReturnedScheduler()
        val localTasks = sampleLocalCompletedTasks()
        val throwable = Throwable()
        taskRemoteDataSubject.onError(throwable)
        putTasksIntoLocalDataSource(localTasks)

        val testObserver = subscribeToCompletedTasksEvent()
        testScheduler.triggerActions()

        testObserver.assertReceivedOnNext(listOf(
                emptyIdleEvent(),
                emptyLoadingEvent(),
                loadingEventWith(localTasks),
                errorEventWith(localTasks, throwable)
        ))
    }

    @Test
    fun `Given the tasks are all activated, On getCompletedTasksEvent, It should return empty`() {
        val testScheduler = givenPresenterWithReturnedScheduler()
        val tasks = sampleLocalActivatedTasks()
        taskRemoteDataSubject.onCompleted()
        putTasksIntoLocalDataSource(tasks)

        val testObserver = subscribeToCompletedTasksEvent()
        testScheduler.triggerActions()

        testObserver.assertReceivedOnNext(listOf(
                emptyIdleEvent(),
                emptyLoadingEvent(),
                emptyLoadingEvent(),
                emptyIdleEvent()
        ))
    }

    @Test
    fun `Given some tasks are activated, On getCompletedTasksEvent, It should filter tasks`() {
        val tasks = sampleLocalSomeCompletedTasks()
        taskRemoteDataSubject.onCompleted()
        putTasksIntoLocalDataSource(tasks)

        val testObserver = subscribeToCompletedTasksEvent()

        testObserver.assertReceivedOnNext(listOf(
                idleEventWith(sampleLocalCompletedTasks())
        ))
    }


    @Test
    fun `Given the service has completed tasks, On clearCompletedTasks, It should return locally cleared tasks first then confirm`() {
        val tasks = taskListWithSomeCompleted()
        putTaskListIntoRemoteDataSource(tasks)
        taskLocalDataSubject.onCompleted()
        `when`(remoteDataSource.clearCompletedTasks()).thenReturn(Observable.just(remoteTasksWithCompletedTasksRemoved()))

        val testObserver = subscribeToTasksEvent()

        service.clearCompletedTasks()

        testObserver.assertReceivedOnNext(listOf(
                idleEventWith(asSyncedTasks(tasks)), // ideally, we would ignore this
                loadingEventWith(asSyncedTasks(tasks)),
                loadingEventWith(tasksWithCompletedMarkedDeleted(TEST_TIME)),
                loadingEventWith(asSyncedTasks(remoteTasksWithCompletedTasksRemoved())),
                idleEventWith(asSyncedTasks(remoteTasksWithCompletedTasksRemoved()))
        ))
    }

    @Test
    fun `Given the service has pending action more recent than current one, On clearCompletedTasks, It should not update the pending task`() {
        val (tasks, task) = taskListWithOneAhead(taskListWithSomeCompleted(), 1)

        val tasksWithDeletedLocallyAndAheadData = Tasks.from(ImmutableList.copyOf(listOf(
                SyncedData.from(Task.builder().id(Id.from("24")).title("Bar").isCompleted(true).build(), SyncState.DELETED_LOCALLY, 321),
                SyncedData.from(Task.builder().id(Id.from("42")).title("Foo").build(), SyncState.AHEAD, 456),
                SyncedData.from(Task.builder().id(Id.from("424")).title("New").isCompleted(true).build(), SyncState.DELETED_LOCALLY, 321),
                SyncedData.from(Task.builder().id(Id.from("425")).title("Whizz").build(), SyncState.AHEAD, 321)
        )))

        val untouchedAheadData = Tasks.from(ImmutableList.copyOf(listOf(
                SyncedData.from(Task.builder().id(Id.from("42")).title("Foo").build(), SyncState.AHEAD, 456)
        )))

        taskRemoteDataSubject.onCompleted()
        putTasksIntoLocalDataSource(tasks)
        `when`(clock.timeInMillis()).thenReturn(321)
        `when`(remoteDataSource.clearCompletedTasks()).thenReturn(Observable.just(remoteTasksWithCompletedTasksRemoved()))
        val testObserver = subscribeToTasksEvent()

        service.clearCompletedTasks()

        testObserver.assertReceivedOnNext(listOf(
                idleEventWith(tasks),
                loadingEventWith(tasks),
                loadingEventWith(tasksWithDeletedLocallyAndAheadData),
                loadingEventWith(untouchedAheadData),
                idleEventWith(untouchedAheadData)
        ))
    }

    @Test
    fun `Given the service has completed pending action more recent than current one, On clearCompletedTasks, It should not update the pending task`() {
        val (tasks, task) = taskListWithOneAhead(taskListWithSomeCompleted(), 0)

        val tasksWithDeletedLocallyAndAheadData = Tasks.from(ImmutableList.copyOf(listOf(
                SyncedData.from(Task.builder().id(Id.from("24")).title("Bar").isCompleted(true).build(), SyncState.AHEAD, 456),
                SyncedData.from(Task.builder().id(Id.from("42")).title("Foo").build(), SyncState.AHEAD, 321),
                SyncedData.from(Task.builder().id(Id.from("424")).title("New").isCompleted(true).build(), SyncState.DELETED_LOCALLY, 321),
                SyncedData.from(Task.builder().id(Id.from("425")).title("Whizz").build(), SyncState.AHEAD, 321)
        )))

        val untouchedAheadData = Tasks.from(ImmutableList.copyOf(listOf(
                SyncedData.from(Task.builder().id(Id.from("24")).title("Bar").isCompleted(true).build(), SyncState.AHEAD, 456),
                SyncedData.from(Task.builder().id(Id.from("42")).title("Foo").build(), SyncState.IN_SYNC, 321)
        )))

        taskRemoteDataSubject.onCompleted()
        putTasksIntoLocalDataSource(tasks)
        `when`(clock.timeInMillis()).thenReturn(321)
        `when`(remoteDataSource.clearCompletedTasks()).thenReturn(Observable.just(remoteTasksWithCompletedTasksRemoved()))
        val testObserver = subscribeToTasksEvent()

        service.clearCompletedTasks()

        testObserver.assertReceivedOnNext(listOf(
                idleEventWith(tasks),
                loadingEventWith(tasks),
                loadingEventWith(tasksWithDeletedLocallyAndAheadData),
                loadingEventWith(untouchedAheadData),
                idleEventWith(untouchedAheadData)
        ))
    }

    @Test
    fun `Given the remote fails to clear completed tasks, On clearCompletedTasks, It should return locally cleared first then SyncError`() {
        val tasks = taskListWithSomeCompleted()
        putTaskListIntoRemoteDataSource(tasks)
        taskLocalDataSubject.onCompleted()
        `when`(remoteDataSource.clearCompletedTasks()).thenReturn(Observable.error(Throwable("Terrible things")))

        val testObserver = subscribeToTasksEvent()

        service.clearCompletedTasks()

        testObserver.assertReceivedOnNext(listOf(
                idleEventWith(asSyncedTasks(tasks)),
                loadingEventWith(asSyncedTasks(tasks)),
                loadingEventWith(tasksWithCompletedMarkedDeleted(TEST_TIME)),
                idleEventWith(tasksWithFirstAndLastAsError()).asError(SyncError())
        ))
    }

    @Test
    fun `Given the service has tasks, On deleteTask, It should return all tasks except that one`() {
        `when`(remoteDataSource.deleteTask(any())).thenReturn(Observable.empty())

        val initialRemoteTaskList = taskListWithSomeCompleted()

        putTaskListIntoRemoteDataSource(initialRemoteTaskList)
        taskLocalDataSubject.onCompleted()

        val tasksFromRemote = asSyncedTasks(initialRemoteTaskList)

        val loadingTasks = firstTaskDeletedLocally()

        val finalTasks = asSyncedTasks(listOf(
                Task.builder().id(Id.from("42")).title("Foo").build(),
                Task.builder().id(Id.from("424")).title("New").isCompleted(true).build(),
                Task.builder().id(Id.from("425")).title("Whizz").build()
        ))

        val testObserver = subscribeToTasksEvent()
        service.delete(initialRemoteTaskList.get(0))

        testObserver.assertReceivedOnNext(listOf(
                idleEventWith(tasksFromRemote), // previous state, ignore this
                loadingEventWith(loadingTasks),
                idleEventWith(finalTasks)
        ))
    }

    @Test
    fun `Given the remote fails to delete task, On deleteTask, It should mark deleted task as SyncError`() {
        `when`(remoteDataSource.deleteTask(any())).thenReturn(Observable.error(Throwable("oh no")))

        val initialRemoteTaskList = taskListWithSomeCompleted()

        putTaskListIntoRemoteDataSource(initialRemoteTaskList)
        taskLocalDataSubject.onCompleted()

        val tasksFromRemote = asSyncedTasks(initialRemoteTaskList)

        val loadingTasks = firstTaskDeletedLocally()

        val finalTasks = firstTaskAsError()

        val testObserver = subscribeToTasksEvent()
        service.delete(initialRemoteTaskList.get(0))

        testObserver.assertReceivedOnNext(listOf(
                idleEventWith(tasksFromRemote), // previous state, ignore this
                loadingEventWith(loadingTasks),
                errorEventWith(finalTasks, SyncError())
        ))
    }

    @Test
    fun `Given we have an outdated response from remote, On deleteTask, It should not delete the task`() {
        `when`(remoteDataSource.deleteTask(any())).thenReturn(Observable.empty())

        val localTasks = firstTaskAhead()

        putTasksIntoLocalDataSource(localTasks)
        taskRemoteDataSubject.onCompleted()

        val testObserver = subscribeToTasksEvent()
        service.delete(localTasks.all().get(0).data())

        testObserver.assertReceivedOnNext(listOf(
                idleEventWith(localTasks)
        ))
    }

    private fun putTasksIntoLocalDataSource(localTasks: Tasks) {
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
    }

    private fun putTaskListIntoRemoteDataSource(remoteTasks: List<Task>) {
        taskRemoteDataSubject.onNext(remoteTasks)
        taskRemoteDataSubject.onCompleted()
    }

    private fun idleEventWith(tasks: Tasks) = emptyIdleEvent().updateData(tasks)

    private fun emptyIdleEvent() = Event.idle<Tasks>(noEmptyTasks())

    private fun loadingEventWith(tasks: Tasks) = emptyLoadingEvent().updateData(tasks)

    private fun emptyLoadingEvent() = Event.loading<Tasks>(noEmptyTasks())

    private fun errorEventWith(tasks: Tasks, throwable: Throwable) = validatedErrorEvent(defaultErrorEvent(tasks, throwable))

    private fun emptyErrorEvent(throwable: Throwable) = validatedErrorEvent(defaultErrorEvent(throwable))

    // don't use these in the tests, use errorEventWith or emptyErrorEvent
    private fun validatedErrorEvent(errorEvent: Event<Tasks>) = errorEvent.toBuilder().dataValidator(noEmptyTasks()).build()

    private fun defaultErrorEvent(localTasks: Tasks, throwable: Throwable) = defaultErrorEvent(throwable).updateData(localTasks)

    private fun defaultErrorEvent(throwable: Throwable) = Event.error<Tasks>(throwable)

    private fun subscribeToTasksEvent(): TestObserver<Event<Tasks>> {
        val testObserver = TestObserver<Event<Tasks>>()
        service.tasksEvent.subscribe(testObserver)
        return testObserver
    }

    private fun subscribeToActiveTasksEvent(): TestObserver<Event<Tasks>> {
        val testObserver = TestObserver<Event<Tasks>>()
        service.activeTasksEvent.subscribe(testObserver)
        return testObserver
    }

    private fun subscribeToCompletedTasksEvent(): TestObserver<Event<Tasks>> {
        val testObserver = TestObserver<Event<Tasks>>()
        service.completedTasksEvent.subscribe(testObserver)
        return testObserver
    }

    private fun sampleRefreshedTasks() = listOf(
            Task.builder().id(Id.from("24")).title("Bar").build(),
            Task.builder().id(Id.from("42")).title("Foo").build(),
            Task.builder().id(Id.from("424")).title("New").build()
    )

    private fun sampleRemoteTasks() = listOf(
            Task.builder().id(Id.from("24")).title("Bar").build(),
            Task.builder().id(Id.from("42")).title("Foo").build()
    )

    private fun taskListWithSomeCompleted() = listOf(
            Task.builder().id(Id.from("24")).title("Bar").isCompleted(true).build(),
            Task.builder().id(Id.from("42")).title("Foo").build(),
            Task.builder().id(Id.from("424")).title("New").isCompleted(true).build(),
            Task.builder().id(Id.from("425")).title("Whizz").build()
    )

    private fun taskListWithOneAhead(tasks: List<Task>, index: Int): Pair<Tasks, Task> {
        val tasksOld = Tasks.asSynced(tasks, TEST_TIME)
        val taskOld = tasksOld.all().get(index)
        val syncedTask = SyncedData.from(taskOld.data(), SyncState.AHEAD, 456)
        val tasks = tasksOld.save(syncedTask)
        return Pair(tasks, syncedTask.data())
    }

    private fun tasksWithCompletedMarkedDeleted(actionTimestamp: Long) = Tasks.from(ImmutableList.copyOf(listOf(
            SyncedData.from(Task.builder().id(Id.from("24")).title("Bar").isCompleted(true).build(), SyncState.DELETED_LOCALLY, actionTimestamp),
            SyncedData.from(Task.builder().id(Id.from("42")).title("Foo").build(), SyncState.AHEAD, actionTimestamp),
            SyncedData.from(Task.builder().id(Id.from("424")).title("New").isCompleted(true).build(), SyncState.DELETED_LOCALLY, actionTimestamp),
            SyncedData.from(Task.builder().id(Id.from("425")).title("Whizz").build(), SyncState.AHEAD, actionTimestamp)
    )))

    private fun tasksWithFirstAndLastAsError() = Tasks.from(ImmutableList.copyOf(listOf(
            SyncedData.from(Task.builder().id(Id.from("24")).title("Bar").isCompleted(true).build(), SyncState.SYNC_ERROR, TEST_TIME),
            SyncedData.from(Task.builder().id(Id.from("42")).title("Foo").build(), SyncState.SYNC_ERROR, TEST_TIME),
            SyncedData.from(Task.builder().id(Id.from("424")).title("New").isCompleted(true).build(), SyncState.SYNC_ERROR, TEST_TIME),
            SyncedData.from(Task.builder().id(Id.from("425")).title("Whizz").build(), SyncState.SYNC_ERROR, TEST_TIME)
    )))

    private fun remoteTasksWithCompletedTasksRemoved() = listOf(
            Task.builder().id(Id.from("42")).title("Foo").build()
    )

    private fun sampleRemoteCompletedTasks() = listOf(
            Task.builder().id(Id.from("24")).title("Bar").isCompleted(true).build(),
            Task.builder().id(Id.from("42")).title("Foo").isCompleted(true).build()
    )

    private fun sampleLocalTasks() = asSyncedTasks(sampleLocalTaskList())

    private fun sampleLocalTaskList() = listOf(
            Task.builder().id(Id.from("24")).title("Bar").build()
    )

    private fun sampleLocalCompletedTasks() = asSyncedTasks(sampleLocalCompletedTaskList())

    private fun sampleLocalCompletedTaskList() = listOf(
            Task.builder().id(Id.from("42")).title("Foo").isCompleted(true).build()
    )

    private fun sampleLocalActivatedTasks() = asSyncedTasks(listOf(
            Task.builder().id(Id.from("24")).title("Bar").build()
    ))

    private fun sampleLocalSomeCompletedTasks() = asSyncedTasks(listOf(
            Task.builder().id(Id.from("24")).title("Bar").build(),
            Task.builder().id(Id.from("42")).title("Foo").isCompleted(true).build()
    ))

    private fun firstTaskDeletedLocally(): Tasks {
        return firstTaskUpdatedWith(SyncState.DELETED_LOCALLY, TEST_TIME)
    }

    private fun firstTaskAsError(): Tasks {
        return firstTaskUpdatedWith(SyncState.SYNC_ERROR, TEST_TIME)
    }

    private fun firstTaskAhead(): Tasks {
        return firstTaskUpdatedWith(SyncState.AHEAD, TEST_TIME + 1)
    }

    private fun firstTaskUpdatedWith(syncState: SyncState, time: Long): Tasks {
        return Tasks.from(ImmutableList.copyOf(listOf<SyncedData<Task>>(
                SyncedData.from(Task.builder().id(Id.from("24")).title("Bar").isCompleted(true).build(), syncState, time),
                SyncedData.from(Task.builder().id(Id.from("42")).title("Foo").build(), SyncState.IN_SYNC, TEST_TIME),
                SyncedData.from(Task.builder().id(Id.from("424")).title("New").isCompleted(true).build(), SyncState.IN_SYNC, TEST_TIME),
                SyncedData.from(Task.builder().id(Id.from("425")).title("Whizz").build(), SyncState.IN_SYNC, TEST_TIME)
        )))
    }

    private fun asSyncedTasks(remoteTasks: List<Task>) = Tasks.asSynced(remoteTasks, TEST_TIME)

    private fun noEmptyTasks() = NoEmptyTasksPredicate()


    private fun givenPresenterWithReturnedScheduler(): TestScheduler {
        val testScheduler = Schedulers.test()
        service = PersistedTasksService(localDataSource, remoteDataSource, freshnessChecker, clock, testScheduler)
        return testScheduler
    }

    private fun setUpService() {
        taskRemoteDataSubject = BehaviorSubject.create()
        taskLocalDataSubject = BehaviorSubject.create()
        val tasksApiReplay = taskRemoteDataSubject.replay()
        val tasksLocalDataReplay = taskLocalDataSubject.replay()
        tasksApiReplay.connect()
        tasksLocalDataReplay.connect()
        `when`(remoteDataSource.tasks).thenReturn(tasksApiReplay)
        `when`(localDataSource.tasks).thenReturn(tasksLocalDataReplay)
    }
}
