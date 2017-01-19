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

    var service: TasksService = PersistedTasksService(localDataSource, remoteDataSource, freshnessChecker, clock)

    @Before
    fun setUp() {
        setUpService()
        service = PersistedTasksService(localDataSource, remoteDataSource, freshnessChecker, clock)
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
    fun given_TheLocalDataHasTasksAndTasksAreExpired_on_GetTasks_it_ShouldSaveTasksFromTheRemoteInTheLocalData() {
        val remoteTasks = sampleRemoteTasks()
        val localTasks = sampleLocalTasks()
        taskRemoteDataSubject.onNext(remoteTasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(false)

        subscribeToTasksEvent()

        verify(localDataSource).saveTasks(asSyncedTasks(remoteTasks))
    }


    @Test
    fun given_TheLocalDataIsEmpty_on_GetTasksEvents_it_ShouldReturnTasksFromTheRemote() {
        val tasks = sampleRemoteTasks()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()

        val testObserver = subscribeToTasksEvent()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                loadingEventWith(asSyncedTasks(tasks)),
                idleEventWith(asSyncedTasks(tasks))
        ))
    }

    @Test
    fun given_TheLocalDataHasTasksAndTasksAreFresh_on_GetTasksEvents_it_ShouldReturnTasksFromTheLocalData() {
        val remoteTasks = sampleRemoteTasks()
        val localTasks = sampleLocalTasks()
        taskRemoteDataSubject.onNext(remoteTasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)

        val testObserver = subscribeToTasksEvent()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                loadingEventWith(localTasks),
                idleEventWith(localTasks)
        ))
    }

    @Test
    fun given_TheLocalDataHasTasksAndTasksAreExpired_on_GetTasksEvents_it_ShouldReturnTasksFromTheLocalDataThenTasksFromTheRemote() {
        val remoteTasks = sampleRemoteTasks()
        val localTasks = sampleLocalTasks()
        taskRemoteDataSubject.onNext(remoteTasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(false)

        val testObserver = subscribeToTasksEvent()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                loadingEventWith(localTasks),
                loadingEventWith(asSyncedTasks(remoteTasks)),
                idleEventWith(asSyncedTasks(remoteTasks))
        ))
    }

    @Test
    fun given_TheLocalDataIsEmptyAndRemoteFails_on_GetTasksEvents_it_ShouldReturnError() {
        val throwable = Throwable()
        taskRemoteDataSubject.onError(throwable)
        taskLocalDataSubject.onCompleted()

        val testObserver = subscribeToTasksEvent()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                emptyErrorEvent(throwable)
        ))
    }

    @Test
    fun given_TheLocalDataIsEmptyAndRemoteIsEmpty_on_GetTasksEvents_it_ShouldReturnEmpty() {
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()

        val testObserver = subscribeToTasksEvent()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                emptyIdleEvent()
        ))
    }

    @Test
    fun given_TheLocalDataFailsAndRemoteIsEmpty_on_GetTasksEvents_it_ShouldReturnError() {
        val throwable = Throwable()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onError(throwable)

        val testObserver = subscribeToTasksEvent()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                emptyErrorEvent(throwable)
        ))
    }

    @Test
    fun given_TheLocalDataFailsAndRemoteHasData_on_GetTasksEvents_it_ShouldReturnError() {
        val tasks = sampleRemoteTasks()
        val throwable = Throwable()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onError(throwable)

        val testObserver = subscribeToTasksEvent()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                emptyErrorEvent(throwable)
        ))
    }

    @Test
    fun given_TheLocalDataHasDataAndRemoteFails_on_GetTasksEvents_it_ShouldReturnErrorWithData() {
        val localTasks = sampleLocalTasks()
        val throwable = Throwable()
        taskRemoteDataSubject.onError(throwable)
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()

        val testObserver = subscribeToTasksEvent()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                loadingEventWith(localTasks),
                errorEventWith(localTasks, throwable)
        ))
    }

    @Test
    fun given_remoteDataSourceDataHasChanged_on_RefreshTasks_it_ShouldReturnNewData() {
        val tasks = sampleRemoteTasks()
        val tasksRefreshed = sampleRefreshedTasks()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()
        val testObserver = subscribeToTasksEvent()
        setUpService()
        taskRemoteDataSubject.onNext(tasksRefreshed)
        taskRemoteDataSubject.onCompleted()

        service.refreshTasks().call()

        testObserver.assertReceivedOnNext(
                listOf(
                        emptyLoadingEvent(),
                        loadingEventWith(asSyncedTasks(tasks)),
                        idleEventWith(asSyncedTasks(tasks)),
                        loadingEventWith(asSyncedTasks(tasks)),
                        loadingEventWith(asSyncedTasks(tasksRefreshed)),
                        idleEventWith(asSyncedTasks(tasksRefreshed))
                )
        )
    }

    @Test
    fun given_remoteDataSourceDataHasNotChanged_on_RefreshTasks_it_ShouldReturnNoAdditionalData() {
        val tasks = sampleRemoteTasks()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()
        val testObserver = subscribeToTasksEvent()
        setUpService()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()

        service.refreshTasks().call()

        testObserver.assertReceivedOnNext(
                listOf(
                        emptyLoadingEvent(),
                        loadingEventWith(asSyncedTasks(tasks)),
                        idleEventWith(asSyncedTasks(tasks)),
                        loadingEventWith(asSyncedTasks(tasks)),
                        idleEventWith(asSyncedTasks(tasks))
                )
        )
    }

    @Test
    fun given_remoteDataSourceDataHasChanged_on_RefreshTasks_it_ShouldPersistNewDataToLocalDatasitory() {
        val tasks = sampleRemoteTasks()
        val tasksRefreshed = sampleRefreshedTasks()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()
        subscribeToTasksEvent()
        setUpService()
        taskRemoteDataSubject.onNext(tasksRefreshed)
        taskRemoteDataSubject.onCompleted()

        service.refreshTasks().call()

        verify(localDataSource).saveTasks(asSyncedTasks(tasksRefreshed))
    }

    @Test
    fun given_ServiceHasAlreadySentData_on_RefreshTasks_it_ShouldRestartLoading() {
        val tasks = sampleRemoteTasks()
        val tasksRefreshed = sampleRefreshedTasks()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()
        val testObserver = subscribeToTasksEvent()
        setUpService()
        taskRemoteDataSubject.onNext(tasksRefreshed)
        taskRemoteDataSubject.onCompleted()

        service.refreshTasks().call()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                loadingEventWith(asSyncedTasks(tasks)),
                idleEventWith(asSyncedTasks(tasks)),
                loadingEventWith(asSyncedTasks(tasks)),
                loadingEventWith(asSyncedTasks(tasksRefreshed)),
                idleEventWith(asSyncedTasks(tasksRefreshed))
        ))
    }

    @Test
    fun given_RemoteIsAcceptingUpdates_on_CompleteTask_it_ShouldSendAheadThenInSyncInfo() {
        val localTasks = sampleLocalTasks()
        val task = localTasks.all().get(0).data()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321);
        val testObserver = subscribeToTasksEvent()

        service.complete(task).call()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                loadingEventWith(localTasks),
                idleEventWith(localTasks),
                idleEventWith(localTasks.save(SyncedData.from(task.complete(), SyncState.AHEAD, 321))),
                idleEventWith(localTasks.save(SyncedData.from(task.complete(), SyncState.IN_SYNC, 321)))
        ))
        assertTrue(testObserver.onCompletedEvents.isEmpty(), "Service stream should never terminate")
    }

    @Test
    fun given_ServiceHasPendingActionMoreRecentThanCurrentOne_on_CompleteTask_it_ShouldSkipTheUpdatesForCurrentAction() {
        val tasksOld = sampleLocalTasks()
        val taskOld = tasksOld.all().get(0)
        val syncedTask = SyncedData.from(taskOld.data(), taskOld.syncState(), 456)
        val updatedTasks = tasksOld.save(syncedTask)
        val updatedTask = syncedTask.data()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(updatedTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(updatedTasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321);

        val testObserver = subscribeToTasksEvent()

        service.complete(updatedTask).call()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                loadingEventWith(updatedTasks),
                idleEventWith(updatedTasks)
        ))
        assertTrue(testObserver.onCompletedEvents.isEmpty(), "Service stream should never terminate")
    }

    @Test
    fun given_RemoteIsFailingUpdates_on_CompleteTask_it_ShouldSendAheadThenThenMarkAsError() {
        val localTasks = sampleLocalTasks()
        val task = localTasks.all().get(0).data()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321);
        Mockito.doAnswer {
            Observable.error<Task>(Throwable())
        }.`when`(remoteDataSource).saveTask(any())

        val testObserver = subscribeToTasksEvent()

        service.complete(task).call()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                loadingEventWith(localTasks),
                idleEventWith(localTasks),
                idleEventWith(localTasks.save(SyncedData.from(task.complete(), SyncState.AHEAD, 321))),
                idleEventWith(localTasks.save(SyncedData.from(task.complete(), SyncState.SYNC_ERROR, 321)))
        ))
        assertTrue(testObserver.onCompletedEvents.isEmpty(), "Service stream should never terminate")
    }

    @Test
    fun given_RemoteIsAcceptingUpdates_on_ActivateTask_it_ShouldSendAheadThenInSyncInfo() {
        val localTasks = sampleLocalCompletedTasks()
        val task = localTasks.all().get(0).data()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321);

        val testObserver = subscribeToTasksEvent()

        service.activate(task).call()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                loadingEventWith(localTasks),
                idleEventWith(localTasks),
                idleEventWith(localTasks.save(SyncedData.from(task.activate(), SyncState.AHEAD, 321))),
                idleEventWith(localTasks.save(SyncedData.from(task.activate(), SyncState.IN_SYNC, 321)))
        ))
        assertTrue(testObserver.onCompletedEvents.isEmpty(), "Service stream should never terminate")
    }

    @Test
    fun given_ServiceHasPendingActionMoreRecentThanCurrentOne_on_ActivateTask_it_ShouldSkipTheUpdatesForCurrentAction() {
        val tasksOld = sampleLocalCompletedTasks()
        val taskOld = tasksOld.all().get(0)
        val syncedTask = SyncedData.from(taskOld.data(), taskOld.syncState(), 456)
        val tasks = tasksOld.save(syncedTask)
        val task = syncedTask.data()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(tasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(tasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321);

        val testObserver = subscribeToTasksEvent()

        service.activate(task).call()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                loadingEventWith(tasks),
                idleEventWith(tasks)
        ))
        assertTrue(testObserver.onCompletedEvents.isEmpty(), "Service stream should never terminate")
    }

    @Test
    fun given_RemoteIsFailingUpdates_on_ActivateTask_it_ShouldSendAheadThenMarkAsError() {
        val localTasks = sampleLocalCompletedTasks()
        val task = localTasks.all().get(0).data()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321);
        Mockito.doAnswer {
            Observable.error<Task>(Throwable())
        }.`when`(remoteDataSource).saveTask(any())

        val testObserver = subscribeToTasksEvent()

        service.activate(task).call()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                loadingEventWith(localTasks),
                idleEventWith(localTasks),
                idleEventWith(localTasks.save(SyncedData.from(task.activate(), SyncState.AHEAD, 321))),
                idleEventWith(localTasks.save(SyncedData.from(task.activate(), SyncState.SYNC_ERROR, 321)))
        ))
        assertTrue(testObserver.onCompletedEvents.isEmpty(), "Service stream should never terminate")
    }

    @Test
    fun given_RemoteIsAcceptingUpdates_on_SaveTask_it_ShouldSendAheadThenInSyncInfo() {
        val localTasks = sampleLocalCompletedTasks()
        val task = localTasks.all().get(0).data()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321);

        val testObserver = subscribeToTasksEvent()

        val newTask = task.toBuilder().description("NewDesc").build()
        service.save(newTask).call()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                loadingEventWith(localTasks),
                idleEventWith(localTasks),
                idleEventWith(localTasks.save(SyncedData.from(newTask, SyncState.AHEAD, 321))),
                idleEventWith(localTasks.save(SyncedData.from(newTask, SyncState.IN_SYNC, 321)))
        ))
        assertTrue(testObserver.onCompletedEvents.isEmpty(), "Service stream should never terminate")
    }

    @Test
    fun given_ServiceHasPendingActionMoreRecentThanCurrentOne_on_SaveTask_it_ShouldSkipTheUpdatesForCurrentAction() {
        val tasksOld = sampleLocalCompletedTasks()
        val taskOld = tasksOld.all().get(0)
        val syncedTask = SyncedData.from(taskOld.data(), taskOld.syncState(), 456)
        val tasks = tasksOld.save(syncedTask)
        val task = syncedTask.data()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(tasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(tasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321);

        val testObserver = subscribeToTasksEvent()

        val newTask = task.toBuilder().description("NewDesc").build()
        service.save(newTask).call()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                loadingEventWith(tasks),
                idleEventWith(tasks)
        ))
        assertTrue(testObserver.onCompletedEvents.isEmpty(), "Service stream should never terminate")
    }

    @Test
    fun given_RemoteIsFailingUpdates_on_SaveTask_it_ShouldSendAheadThenMarkAsError() {
        val localTasks = sampleLocalCompletedTasks()
        val task = localTasks.all().get(0).data()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321);
        Mockito.doAnswer {
            Observable.error<Task>(Throwable())
        }.`when`(remoteDataSource).saveTask(any())

        val testObserver = subscribeToTasksEvent()

        val newTask = task.toBuilder().description("NewDesc").build()
        service.save(newTask).call()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                loadingEventWith(localTasks),
                idleEventWith(localTasks),
                idleEventWith(localTasks.save(SyncedData.from(newTask, SyncState.AHEAD, 321))),
                idleEventWith(localTasks.save(SyncedData.from(newTask, SyncState.SYNC_ERROR, 321)))
        ))
        assertTrue(testObserver.onCompletedEvents.isEmpty(), "Service stream should never terminate")
    }

    @Test
    fun given_TheLocalDataIsEmpty_on_GetActiveTasks_it_ShouldSaveTasksFromTheRemoteInTheLocalData() {
        val tasks = sampleRemoteTasks()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()

        subscribeToActiveTasksEvent()

        verify(localDataSource).saveTasks(asSyncedTasks(tasks))
    }

    @Test
    fun given_TheLocalDataHasTasksAndTasksAreExpired_on_GetActiveTasks_it_ShouldSaveTasksFromTheRemoteInTheLocalData() {
        val remoteTasks = sampleRemoteTasks()
        val localTasks = sampleLocalTasks()
        taskRemoteDataSubject.onNext(remoteTasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(false)

        subscribeToActiveTasksEvent()

        verify(localDataSource).saveTasks(asSyncedTasks(remoteTasks))
    }

    @Test
    fun given_TheLocalDataIsEmpty_on_GetActiveTasksEvents_it_ShouldReturnTasksFromTheRemote() {
        val tasks = sampleRemoteTasks()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()

        val testObserver = subscribeToActiveTasksEvent()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                loadingEventWith(asSyncedTasks(tasks)),
                idleEventWith(asSyncedTasks(tasks))
        ))
    }

    @Test
    fun given_TheLocalDataHasTasksAndTasksAreFresh_on_GetActiveTasksEvents_it_ShouldReturnTasksFromTheLocalData() {
        val remoteTasks = sampleRemoteTasks()
        val localTasks = sampleLocalTasks()
        taskRemoteDataSubject.onNext(remoteTasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)

        val testObserver = subscribeToActiveTasksEvent()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                loadingEventWith(localTasks),
                idleEventWith(localTasks)
        ))
    }

    @Test
    fun given_TheLocalDataHasTasksAndTasksAreExpired_on_GetActiveTasksEvents_it_ShouldReturnTasksFromTheLocalDataThenTasksFromTheRemote() {
        val remoteTasks = sampleRemoteTasks()
        val localTasks = sampleLocalTasks()
        taskRemoteDataSubject.onNext(remoteTasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(false)

        val testObserver = subscribeToActiveTasksEvent()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                loadingEventWith(localTasks),
                loadingEventWith(asSyncedTasks(remoteTasks)),
                idleEventWith(asSyncedTasks(remoteTasks))
        ))
    }

    @Test
    fun given_TheLocalDataIsEmptyAndRemoteFails_on_GetActiveTasksEvents_it_ShouldReturnError() {
        val throwable = Throwable()
        taskRemoteDataSubject.onError(throwable)
        taskLocalDataSubject.onCompleted()

        val testObserver = subscribeToActiveTasksEvent()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                emptyErrorEvent(throwable)
        ))
    }

    @Test
    fun given_TheLocalDataIsEmptyAndRemoteIsEmpty_on_GetActiveTasksEvents_it_ShouldReturnEmpty() {
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()

        val testObserver = subscribeToActiveTasksEvent()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                emptyIdleEvent()
        ))
    }

    @Test
    fun given_TheLocalDataFailsAndRemoteIsEmpty_on_GetActiveTasksEvents_it_ShouldReturnError() {
        val throwable = Throwable()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onError(throwable)

        val testObserver = subscribeToActiveTasksEvent()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                emptyErrorEvent(throwable)
        ))
    }

    @Test
    fun given_TheLocalDataFailsAndRemoteHasData_on_GetActiveTasksEvents_it_ShouldReturnError() {
        val tasks = sampleRemoteTasks()
        val throwable = Throwable()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onError(throwable)

        val testObserver = subscribeToActiveTasksEvent()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                emptyErrorEvent(throwable)
        ))
    }

    @Test
    fun given_TheLocalDataHasDataAndRemoteFails_on_GetActiveTasksEvents_it_ShouldReturnErrorWithData() {
        val localTasks = sampleLocalTasks()
        val throwable = Throwable()
        taskRemoteDataSubject.onError(throwable)
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()

        val testObserver = subscribeToActiveTasksEvent()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                loadingEventWith(localTasks),
                errorEventWith(localTasks, throwable)
        ))
    }

    @Test
    fun given_TheTasksAreAllCompleted_on_GetActiveTasksEvents_it_ShouldReturnEmpty() {
        val tasks = sampleLocalCompletedTasks()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(tasks)
        taskLocalDataSubject.onCompleted()

        val testObserver = subscribeToActiveTasksEvent()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                emptyLoadingEvent(),
                emptyIdleEvent()
        ))
    }

    @Test
    fun given_TheTasksSomeTasksAreCompleted_on_GetActiveTasksEvents_it_ShouldFilterTasks() {
        val tasks = sampleLocalSomeCompletedTasks()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(tasks)
        taskLocalDataSubject.onCompleted()

        val testObserver = subscribeToActiveTasksEvent()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                loadingEventWith(sampleLocalActivatedTasks()),
                idleEventWith(sampleLocalActivatedTasks())
        ))
    }

    @Test
    fun given_TheLocalDataIsEmpty_on_GetCompletedTasks_it_ShouldSaveTasksFromTheRemoteInTheLocalData() {
        val tasks = sampleRemoteCompletedTasks()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()

        subscribeToCompletedTasksEvent()

        verify(localDataSource).saveTasks(asSyncedTasks(tasks))
    }

    @Test
    fun given_TheLocalDataHasTasksAndTasksAreExpired_on_GetCompletedTasks_it_ShouldSaveTasksFromTheRemoteInTheLocalData() {
        val remoteTasks = sampleRemoteCompletedTasks()
        val localTasks = sampleLocalCompletedTasks()
        taskRemoteDataSubject.onNext(remoteTasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(false)

        subscribeToCompletedTasksEvent()

        verify(localDataSource).saveTasks(asSyncedTasks(remoteTasks))
    }

    @Test
    fun given_TheLocalDataIsEmpty_on_GetCompletedTasksEvents_it_ShouldReturnTasksFromTheRemote() {
        val tasks = sampleRemoteCompletedTasks()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()

        val testObserver = subscribeToCompletedTasksEvent()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                loadingEventWith(asSyncedTasks(tasks)),
                idleEventWith(asSyncedTasks(tasks))
        ))
    }

    @Test
    fun given_TheLocalDataHasTasksAndTasksAreFresh_on_GetCompletedTasksEvents_it_ShouldReturnTasksFromTheLocalData() {
        val remoteTasks = sampleRemoteCompletedTasks()
        val localTasks = sampleLocalCompletedTasks()
        taskRemoteDataSubject.onNext(remoteTasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)

        val testObserver = subscribeToCompletedTasksEvent()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                loadingEventWith(localTasks),
                idleEventWith(localTasks)
        ))
    }

    @Test
    fun given_TheLocalDataHasTasksAndTasksAreExpired_on_GetCompletedTasksEvents_it_ShouldReturnTasksFromTheLocalDataThenTasksFromTheRemote() {
        val remoteTasks = sampleRemoteCompletedTasks()
        val localTasks = sampleLocalCompletedTasks()
        taskRemoteDataSubject.onNext(remoteTasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(false)

        val testObserver = subscribeToCompletedTasksEvent()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                loadingEventWith(localTasks),
                loadingEventWith(asSyncedTasks(remoteTasks)),
                idleEventWith(asSyncedTasks(remoteTasks))
        ))
    }

    @Test
    fun given_TheLocalDataIsEmptyAndRemoteFails_on_GetCompletedTasksEvents_it_ShouldReturnError() {
        val throwable = Throwable()
        taskRemoteDataSubject.onError(throwable)
        taskLocalDataSubject.onCompleted()

        val testObserver = subscribeToCompletedTasksEvent()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                emptyErrorEvent(throwable)
        ))
    }

    @Test
    fun given_TheLocalDataIsEmptyAndRemoteIsEmpty_on_GetCompletedTasksEvents_it_ShouldReturnEmpty() {
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()

        val testObserver = subscribeToCompletedTasksEvent()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                emptyIdleEvent()
        ))
    }

    @Test
    fun given_TheLocalDataFailsAndRemoteIsEmpty_on_GetCompletedTasksEvents_it_ShouldReturnError() {
        val throwable = Throwable()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onError(throwable)

        val testObserver = subscribeToCompletedTasksEvent()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                emptyErrorEvent(throwable)
        ))
    }

    @Test
    fun given_TheLocalDataFailsAndRemoteHasData_on_GetCompletedTasksEvents_it_ShouldReturnError() {
        val tasks = sampleRemoteCompletedTasks()
        val throwable = Throwable()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onError(throwable)

        val testObserver = subscribeToCompletedTasksEvent()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                emptyErrorEvent(throwable)
        ))
    }

    @Test
    fun given_TheLocalDataHasDataAndRemoteFails_on_GetCompletedTasksEvents_it_ShouldReturnErrorWithData() {
        val localTasks = sampleLocalCompletedTasks()
        val throwable = Throwable()
        taskRemoteDataSubject.onError(throwable)
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()

        val testObserver = subscribeToCompletedTasksEvent()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                loadingEventWith(localTasks),
                errorEventWith(localTasks, throwable)
        ))
    }

    @Test
    fun given_TheTasksAreAllActivated_on_GetCompletedTasksEvents_it_ShouldReturnEmpty() {
        val tasks = sampleLocalActivatedTasks()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(tasks)
        taskLocalDataSubject.onCompleted()

        val testObserver = subscribeToCompletedTasksEvent()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                emptyLoadingEvent(),
                emptyIdleEvent()
        ))
    }

    @Test
    fun given_TheTasksSomeTasksAreCompleted_on_GetCompletedTasksEvents_it_ShouldFilterTasks() {
        val tasks = sampleLocalSomeCompletedTasks()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(tasks)
        taskLocalDataSubject.onCompleted()

        val testObserver = subscribeToCompletedTasksEvent()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                loadingEventWith(sampleLocalCompletedTasks()),
                idleEventWith(sampleLocalCompletedTasks())
        ))
    }


    @Test
    fun given_WeHaveTasksInTheService_on_ClearCompletedTasks_it_ShouldReturnLocallyClearedTaskFirstThenConfirm() {
        val tasks = sampleRemoteSomeCompletedTasks()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()
        service.getTasksEvent().subscribe(TestObserver<Event<Tasks>>())
        `when`(remoteDataSource.clearCompletedTasks()).thenReturn(Observable.just(sampleRemoteSomeCompletedTasksDeleted()));

        val testObserver = subscribeToTasksEvent()

        service.clearCompletedTasks().call();

        testObserver.assertReceivedOnNext(listOf(
                idleEventWith(asSyncedTasks(tasks)),
                loadingEventWith(sampleLocalSomeCompletedTasksDeleted()),
                loadingEventWith(asSyncedTasks(sampleRemoteSomeCompletedTasksDeleted())),
                idleEventWith(asSyncedTasks(sampleRemoteSomeCompletedTasksDeleted()))
        ))
    }

    @Test
    fun given_RemoteSourceFailsToClearCompletedTasks_on_ClearCompletedTasks_it_ShouldReturnLocallyClearedTaskFirstThenSyncError() {
        val tasks = sampleRemoteSomeCompletedTasks()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()

        service.getTasksEvent().subscribe(TestObserver<Event<Tasks>>())
        `when`(remoteDataSource.clearCompletedTasks()).thenReturn(Observable.error(Throwable("Terrible things")));

        val testObserver = subscribeToTasksEvent()

        service.clearCompletedTasks().call();

        testObserver.assertReceivedOnNext(listOf(
                idleEventWith(asSyncedTasks(tasks)),
                loadingEventWith(sampleLocalSomeCompletedTasksDeleted()),
                idleEventWith(sampleSomeCompletedTasksDeletedFailed()).asError(SyncError())
        ))
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
        service.getTasksEvent().subscribe(testObserver)
        return testObserver
    }

    private fun subscribeToActiveTasksEvent(): TestObserver<Event<Tasks>> {
        val testObserver = TestObserver<Event<Tasks>>()
        service.getActiveTasksEvent().subscribe(testObserver)
        return testObserver
    }

    private fun subscribeToCompletedTasksEvent(): TestObserver<Event<Tasks>> {
        val testObserver = TestObserver<Event<Tasks>>()
        service.getCompletedTasksEvent().subscribe(testObserver)
        return testObserver
    }

    private fun sampleRefreshedTasks() = listOf(
            Task.builder().id(Id.from("42")).title("Foo").build(),
            Task.builder().id(Id.from("24")).title("Bar").build(),
            Task.builder().id(Id.from("424")).title("New").build()
    )

    private fun sampleRemoteTasks() = listOf(
            Task.builder().id(Id.from("42")).title("Foo").build(),
            Task.builder().id(Id.from("24")).title("Bar").build()
    )

    private fun sampleRemoteSomeCompletedTasks() = listOf(
            Task.builder().id(Id.from("24")).title("Bar").isCompleted(true).build(),
            Task.builder().id(Id.from("42")).title("Foo").build(),
            Task.builder().id(Id.from("12")).title("Whizz").build(),
            Task.builder().id(Id.from("424")).title("New").isCompleted(true).build()
    )

    private fun sampleLocalSomeCompletedTasksDeleted() = Tasks.from(ImmutableList.copyOf(listOf(
            SyncedData.from(Task.builder().id(Id.from("24")).title("Bar").isCompleted(true).build(), SyncState.DELETED_LOCALLY, TEST_TIME),
            SyncedData.from(Task.builder().id(Id.from("42")).title("Foo").build(), SyncState.AHEAD, TEST_TIME),
            SyncedData.from(Task.builder().id(Id.from("12")).title("Whizz").build(), SyncState.AHEAD, TEST_TIME),
            SyncedData.from(Task.builder().id(Id.from("424")).title("New").isCompleted(true).build(), SyncState.DELETED_LOCALLY, TEST_TIME)
    )))

    private fun sampleSomeCompletedTasksDeletedFailed() = Tasks.from(ImmutableList.copyOf(listOf(
            SyncedData.from(Task.builder().id(Id.from("24")).title("Bar").isCompleted(true).build(), SyncState.SYNC_ERROR, TEST_TIME),
            SyncedData.from(Task.builder().id(Id.from("42")).title("Foo").build(), SyncState.SYNC_ERROR, TEST_TIME),
            SyncedData.from(Task.builder().id(Id.from("12")).title("Whizz").build(), SyncState.SYNC_ERROR, TEST_TIME),
            SyncedData.from(Task.builder().id(Id.from("424")).title("New").isCompleted(true).build(), SyncState.SYNC_ERROR, TEST_TIME)
    )))

    private fun sampleRemoteSomeCompletedTasksDeleted() = listOf(
            Task.builder().id(Id.from("42")).title("Foo").build()
    )

    private fun sampleRemoteCompletedTasks() = listOf(
            Task.builder().id(Id.from("42")).title("Foo").isCompleted(true).build(),
            Task.builder().id(Id.from("24")).title("Bar").isCompleted(true).build()
    )

    private fun sampleLocalTasks() = asSyncedTasks(listOf(
            Task.builder().id(Id.from("24")).title("Bar").build()
    ))

    private fun sampleLocalCompletedTasks() = asSyncedTasks(listOf(
            Task.builder().id(Id.from("42")).title("Foo").isCompleted(true).build()
    ))

    private fun sampleLocalActivatedTasks() = asSyncedTasks(listOf(
            Task.builder().id(Id.from("24")).title("Bar").build()
    ))

    private fun sampleLocalSomeCompletedTasks() = asSyncedTasks(listOf(
            Task.builder().id(Id.from("42")).title("Foo").isCompleted(true).build(),
            Task.builder().id(Id.from("24")).title("Bar").build()
    ))

    private fun asSyncedTasks(remoteTasks: List<Task>) = Tasks.asSynced(remoteTasks, TEST_TIME)

    private fun noEmptyTasks() = NoEmptyTasksPredicate()

    private fun setUpService() {
        taskRemoteDataSubject = BehaviorSubject.create()
        taskLocalDataSubject = BehaviorSubject.create()
        val tasksApiReplay = taskRemoteDataSubject.replay()
        val tasksLocalDataReplay = taskLocalDataSubject.replay()
        tasksApiReplay.connect()
        tasksLocalDataReplay.connect()
        `when`(remoteDataSource.getTasks()).thenReturn(tasksApiReplay)
        `when`(localDataSource.getTasks()).thenReturn(tasksLocalDataReplay)
    }
}
