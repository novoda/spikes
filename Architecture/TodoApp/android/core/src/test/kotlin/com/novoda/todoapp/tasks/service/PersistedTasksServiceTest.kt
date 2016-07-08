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
        putTaskListIntoRemoteDataSource(remoteTasks)
        putTasksIntoLocalDataSource(localTasks)
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(false)

        subscribeToTasksEvent()

        verify(localDataSource).saveTasks(asSyncedTasks(remoteTasks))
    }


    @Test
    fun given_TheLocalDataIsEmpty_on_GetTasksEvents_it_ShouldReturnTasksFromTheRemote() {
        val tasks = sampleRemoteTasks()
        putTaskListIntoRemoteDataSource(tasks)
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
        putTaskListIntoRemoteDataSource(remoteTasks)
        putTasksIntoLocalDataSource(localTasks)
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
        putTaskListIntoRemoteDataSource(remoteTasks)
        putTasksIntoLocalDataSource(localTasks)
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
        putTaskListIntoRemoteDataSource(tasks)
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
        putTasksIntoLocalDataSource(localTasks)

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
        putTaskListIntoRemoteDataSource(tasks)
        taskLocalDataSubject.onCompleted()

        val testObserver = subscribeToTasksEvent()
        setUpService()
        putTaskListIntoRemoteDataSource(tasksRefreshed)

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
        putTaskListIntoRemoteDataSource(tasks)
        taskLocalDataSubject.onCompleted()

        val testObserver = subscribeToTasksEvent()
        setUpService()
        putTaskListIntoRemoteDataSource(tasks)

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
        putTaskListIntoRemoteDataSource(tasks)
        taskLocalDataSubject.onCompleted()

        subscribeToTasksEvent()
        setUpService()
        putTaskListIntoRemoteDataSource(tasksRefreshed)

        service.refreshTasks().call()

        verify(localDataSource).saveTasks(asSyncedTasks(tasksRefreshed))
    }

    @Test
    fun given_ServiceHasAlreadySentData_on_RefreshTasks_it_ShouldRestartLoading() {
        val tasks = sampleRemoteTasks()
        val tasksRefreshed = sampleRefreshedTasks()
        putTaskListIntoRemoteDataSource(tasks)
        taskLocalDataSubject.onCompleted()

        val testObserver = subscribeToTasksEvent()
        setUpService()
        putTaskListIntoRemoteDataSource(tasksRefreshed)

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
        putTasksIntoLocalDataSource(localTasks)
        taskRemoteDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321)
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
        putTasksIntoLocalDataSource(updatedTasks)
        taskRemoteDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(updatedTasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321)

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
        putTasksIntoLocalDataSource(localTasks)
        taskRemoteDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321)
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
        putTasksIntoLocalDataSource(localTasks)
        taskRemoteDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321)

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
        putTasksIntoLocalDataSource(tasks)
        `when`(freshnessChecker.isFresh(tasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321)

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
        putTasksIntoLocalDataSource(localTasks)
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321)
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
        putTasksIntoLocalDataSource(localTasks)
        taskRemoteDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321)

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
        putTasksIntoLocalDataSource(tasks)
        `when`(freshnessChecker.isFresh(tasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321)

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
        putTasksIntoLocalDataSource(localTasks)
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321)
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
        putTaskListIntoRemoteDataSource(tasks)
        taskLocalDataSubject.onCompleted()

        subscribeToActiveTasksEvent()

        verify(localDataSource).saveTasks(asSyncedTasks(tasks))
    }

    @Test
    fun given_TheLocalDataHasTasksAndTasksAreExpired_on_GetActiveTasks_it_ShouldSaveTasksFromTheRemoteInTheLocalData() {
        val remoteTasks = sampleRemoteTasks()
        val localTasks = sampleLocalTasks()
        putTaskListIntoRemoteDataSource(remoteTasks)
        putTasksIntoLocalDataSource(localTasks)
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(false)

        subscribeToActiveTasksEvent()

        verify(localDataSource).saveTasks(asSyncedTasks(remoteTasks))
    }

    @Test
    fun given_TheLocalDataIsEmpty_on_GetActiveTasksEvents_it_ShouldReturnTasksFromTheRemote() {
        val tasks = sampleRemoteTasks()
        putTaskListIntoRemoteDataSource(tasks)
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
        putTaskListIntoRemoteDataSource(remoteTasks)
        putTasksIntoLocalDataSource(localTasks)
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
        putTaskListIntoRemoteDataSource(remoteTasks)
        putTasksIntoLocalDataSource(localTasks)
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
        putTaskListIntoRemoteDataSource(tasks)
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
        putTasksIntoLocalDataSource(localTasks)

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
        putTasksIntoLocalDataSource(tasks)

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
        putTasksIntoLocalDataSource(tasks)

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
        putTaskListIntoRemoteDataSource(tasks)
        taskLocalDataSubject.onCompleted()

        subscribeToCompletedTasksEvent()

        verify(localDataSource).saveTasks(asSyncedTasks(tasks))
    }

    @Test
    fun given_TheLocalDataHasTasksAndTasksAreExpired_on_GetCompletedTasks_it_ShouldSaveTasksFromTheRemoteInTheLocalData() {
        val remoteTasks = sampleRemoteCompletedTasks()
        val localTasks = sampleLocalCompletedTasks()
        putTaskListIntoRemoteDataSource(remoteTasks)
        putTasksIntoLocalDataSource(localTasks)
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(false)

        subscribeToCompletedTasksEvent()

        verify(localDataSource).saveTasks(asSyncedTasks(remoteTasks))
    }

    @Test
    fun given_TheLocalDataIsEmpty_on_GetCompletedTasksEvents_it_ShouldReturnTasksFromTheRemote() {
        val tasks = sampleRemoteCompletedTasks()
        putTaskListIntoRemoteDataSource(tasks)
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
        putTaskListIntoRemoteDataSource(remoteTasks)
        putTasksIntoLocalDataSource(localTasks)
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
        putTaskListIntoRemoteDataSource(remoteTasks)
        putTasksIntoLocalDataSource(localTasks)
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
        putTaskListIntoRemoteDataSource(tasks)
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
        putTasksIntoLocalDataSource(localTasks)

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
        putTasksIntoLocalDataSource(tasks)

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
        putTasksIntoLocalDataSource(tasks)

        val testObserver = subscribeToCompletedTasksEvent()

        testObserver.assertReceivedOnNext(listOf(
                emptyLoadingEvent(),
                loadingEventWith(sampleLocalCompletedTasks()),
                idleEventWith(sampleLocalCompletedTasks())
        ))
    }


    @Test
    fun given_WeHaveTasksInTheService_on_ClearCompletedTasks_it_ShouldReturnLocallyClearedTaskFirstThenConfirm() {
        val tasks = taskListWithSomeCompleted()
        putTaskListIntoRemoteDataSource(tasks)
        taskLocalDataSubject.onCompleted()
        service.tasksEvent.subscribe(TestObserver<Event<Tasks>>())
        `when`(remoteDataSource.clearCompletedTasks()).thenReturn(Observable.just(remoteTasksWithCompletedTasksRemoved()))

        val testObserver = subscribeToTasksEvent()

        service.clearCompletedTasks().call()

        testObserver.assertReceivedOnNext(listOf(
                idleEventWith(asSyncedTasks(tasks)), // ideally, we would ignore this
                loadingEventWith(tasksWithCompletedMarkedDeleted()),
                loadingEventWith(asSyncedTasks(remoteTasksWithCompletedTasksRemoved())),
                idleEventWith(asSyncedTasks(remoteTasksWithCompletedTasksRemoved()))
        ))
    }

    @Test
    fun given_RemoteSourceFailsToClearCompletedTasks_on_ClearCompletedTasks_it_ShouldReturnLocallyClearedTaskFirstThenSyncError() {
        val tasks = taskListWithSomeCompleted()
        putTaskListIntoRemoteDataSource(tasks)
        taskLocalDataSubject.onCompleted()
        service.tasksEvent.subscribe(TestObserver<Event<Tasks>>())
        `when`(remoteDataSource.clearCompletedTasks()).thenReturn(Observable.error(Throwable("Terrible things")))

        val testObserver = subscribeToTasksEvent()

        service.clearCompletedTasks().call()

        testObserver.assertReceivedOnNext(listOf(
                idleEventWith(asSyncedTasks(tasks)),
                loadingEventWith(tasksWithCompletedMarkedDeleted()),
                idleEventWith(tasksWithFirstAndLastAsError()).asError(SyncError())
        ))
    }

    @Test
    fun given_WeHaveTasksInTheService_on_DeleteTask_it_ShouldReturnIdleEventWithAllTasksExceptThatOne() {
        `when`(remoteDataSource.deleteTask(any())).thenReturn(Observable.empty())

        val initialRemoteTaskList = taskListWithSomeCompleted()

        putTaskListIntoRemoteDataSource(initialRemoteTaskList)
        taskLocalDataSubject.onCompleted()
        service.tasksEvent.subscribe(TestObserver<Event<Tasks>>())

        val tasksFromRemote = asSyncedTasks(initialRemoteTaskList)

        val loadingTasks = firstTaskDeletedLocally()

        val finalTasks = asSyncedTasks(listOf(
                Task.builder().id(Id.from("42")).title("Foo").build(),
                Task.builder().id(Id.from("12")).title("Whizz").build(),
                Task.builder().id(Id.from("424")).title("New").isCompleted(true).build()
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
    fun given_WeHaveAnErrorFromRemote_on_DeleteTask_it_ShouldMarkDeletedTaskAsSyncError() {
        `when`(remoteDataSource.deleteTask(any())).thenReturn(Observable.error(Throwable("oh no")))

        val initialRemoteTaskList = taskListWithSomeCompleted()

        putTaskListIntoRemoteDataSource(initialRemoteTaskList)
        taskLocalDataSubject.onCompleted()
        service.tasksEvent.subscribe(TestObserver<Event<Tasks>>())

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
    fun given_WeHaveAnOutdatedResponseFromRemote_on_DeleteTask_it_ShouldNotDeleteTheTask() {
        `when`(remoteDataSource.deleteTask(any())).thenReturn(Observable.empty())

        val localTasks = firstTaskAhead()

        putTasksIntoLocalDataSource(localTasks)
        taskRemoteDataSubject.onCompleted()
        service.tasksEvent.subscribe(TestObserver<Event<Tasks>>())

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
            Task.builder().id(Id.from("42")).title("Foo").build(),
            Task.builder().id(Id.from("24")).title("Bar").build(),
            Task.builder().id(Id.from("424")).title("New").build()
    )

    private fun sampleRemoteTasks() = listOf(
            Task.builder().id(Id.from("42")).title("Foo").build(),
            Task.builder().id(Id.from("24")).title("Bar").build()
    )

    private fun taskListWithSomeCompleted() = listOf(
            Task.builder().id(Id.from("24")).title("Bar").isCompleted(true).build(),
            Task.builder().id(Id.from("42")).title("Foo").build(),
            Task.builder().id(Id.from("12")).title("Whizz").build(),
            Task.builder().id(Id.from("424")).title("New").isCompleted(true).build()
    )

    private fun tasksWithCompletedMarkedDeleted() = Tasks.from(ImmutableList.copyOf(listOf(
            SyncedData.from(Task.builder().id(Id.from("24")).title("Bar").isCompleted(true).build(), SyncState.DELETED_LOCALLY, TEST_TIME),
            SyncedData.from(Task.builder().id(Id.from("42")).title("Foo").build(), SyncState.AHEAD, TEST_TIME),
            SyncedData.from(Task.builder().id(Id.from("12")).title("Whizz").build(), SyncState.AHEAD, TEST_TIME),
            SyncedData.from(Task.builder().id(Id.from("424")).title("New").isCompleted(true).build(), SyncState.DELETED_LOCALLY, TEST_TIME)
    )))

    private fun tasksWithFirstAndLastAsError() = Tasks.from(ImmutableList.copyOf(listOf(
            SyncedData.from(Task.builder().id(Id.from("24")).title("Bar").isCompleted(true).build(), SyncState.SYNC_ERROR, TEST_TIME),
            SyncedData.from(Task.builder().id(Id.from("42")).title("Foo").build(), SyncState.SYNC_ERROR, TEST_TIME),
            SyncedData.from(Task.builder().id(Id.from("12")).title("Whizz").build(), SyncState.SYNC_ERROR, TEST_TIME),
            SyncedData.from(Task.builder().id(Id.from("424")).title("New").isCompleted(true).build(), SyncState.SYNC_ERROR, TEST_TIME)
    )))

    private fun remoteTasksWithCompletedTasksRemoved() = listOf(
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
                SyncedData.from(Task.builder().id(Id.from("12")).title("Whizz").build(), SyncState.IN_SYNC, TEST_TIME),
                SyncedData.from(Task.builder().id(Id.from("424")).title("New").isCompleted(true).build(), SyncState.IN_SYNC, TEST_TIME)
        )))
    }

    private fun asSyncedTasks(remoteTasks: List<Task>) = Tasks.asSynced(remoteTasks, TEST_TIME)

    private fun noEmptyTasks() = NoEmptyTasksPredicate()

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
