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
    fun given_TheLocalDataIsEmpty_on_GetTasks_it_ShouldReturnTasksFromTheRemote() {
        val tasks = sampleRemoteTasks()
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()

        service.getTasks().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(Tasks.asSynced(tasks, TEST_TIME)))
        assertTrue(testObserver.onCompletedEvents.isEmpty(), "Service stream should never terminate")
    }

    @Test
    fun given_TheLocalDataIsEmpty_on_GetTasks_it_ShouldSaveTasksFromTheRemoteInTheLocalData() {
        val tasks = sampleRemoteTasks()
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()

        service.getTasks().subscribe(testObserver)

        verify(localDataSource).saveTasks(Tasks.asSynced(tasks, TEST_TIME))
    }

    @Test
    fun given_TheLocalDataHasTasksAndTasksAreFresh_on_GetTasks_it_ShouldReturnTasksFromTheLocalData() {
        val remoteTasks = sampleRemoteTasks()
        val localTasks = sampleLocalTasks()
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onNext(remoteTasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)

        service.getTasks().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(localTasks))
    }

    @Test
    fun given_TheLocalDataHasTasksAndTasksAreExpired_on_GetTasks_it_ShouldReturnTasksFromTheLocalDataThenTasksFromTheRemote() {
        val remoteTasks = sampleRemoteTasks()
        val localTasks = sampleLocalTasks()
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onNext(remoteTasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(false)

        service.getTasks().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(localTasks, Tasks.asSynced(remoteTasks, TEST_TIME)))
    }

    @Test
    fun given_TheLocalDataHasTasksAndTasksAreExpired_on_GetTasks_it_ShouldSaveTasksFromTheRemoteInTheLocalData() {
        val remoteTasks = sampleRemoteTasks()
        val localTasks = sampleLocalTasks()
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onNext(remoteTasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(false)

        service.getTasks().subscribe(testObserver)

        verify(localDataSource).saveTasks(Tasks.asSynced(remoteTasks, TEST_TIME))
    }

    @Test
    fun given_TheLocalDataHasTasksAndRemoteFails_on_GetTasks_it_ShouldReturnTasksFromTheLocalData() {
        val localTasks = sampleLocalTasks()
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onError(Throwable())
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(false)

        service.getTasks().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(localTasks))
    }

    @Test
    fun given_TheLocalDataIsEmptyAndRemoteFails_on_GetTasks_it_ShouldReturnNothing() {
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onError(Throwable())
        taskLocalDataSubject.onCompleted()

        service.getTasks().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf())
    }

    @Test
    fun given_TheLocalDataIsEmptyAndRemoteIsEmpty_on_GetTasks_it_ShouldReturnNothing() {
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()

        service.getTasks().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf())
    }

    @Test
    fun given_TheLocalDataFailsAndRemoteIsEmpty_on_GetTasks_it_ShouldReturnNothing() {
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onError(Throwable())

        service.getTasks().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf())
    }

    @Test
    fun given_TheLocalDataFailsAndRemoteHasTasks_on_GetTasks_it_ShouldReturnNothing() {
        val tasks = sampleRemoteTasks()
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onError(Throwable())

        service.getTasks().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf())
    }

    @Test
    fun given_TheLocalDataIsEmpty_on_GetTasksEvents_it_ShouldReturnTasksFromTheRemote() {
        val tasks = sampleRemoteTasks()
        val testObserver = TestObserver<Event<Tasks>>()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()

        service.getTasksEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.loading<Tasks>(noEmptyTasks()),
                Event.loading<Tasks>(noEmptyTasks()).updateData(Tasks.asSynced(tasks, TEST_TIME)),
                Event.idle<Tasks>(noEmptyTasks()).updateData(Tasks.asSynced(tasks, TEST_TIME))
        ))
    }

    @Test
    fun given_TheLocalDataHasTasksAndTasksAreFresh_on_GetTasksEvents_it_ShouldReturnTasksFromTheLocalData() {
        val remoteTasks = sampleRemoteTasks()
        val localTasks = sampleLocalTasks()
        val testObserver = TestObserver<Event<Tasks>>()
        taskRemoteDataSubject.onNext(remoteTasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)

        service.getTasksEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.loading<Tasks>(noEmptyTasks()),
                Event.loading<Tasks>(noEmptyTasks()).updateData(localTasks),
                Event.idle<Tasks>(noEmptyTasks()).updateData(localTasks)
        ))
    }

    @Test
    fun given_TheLocalDataHasTasksAndTasksAreExpired_on_GetTasksEvents_it_ShouldReturnTasksFromTheLocalDataThenTasksFromTheRemote() {
        val remoteTasks = sampleRemoteTasks()
        val localTasks = sampleLocalTasks()
        val testObserver = TestObserver<Event<Tasks>>()
        taskRemoteDataSubject.onNext(remoteTasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(false)

        service.getTasksEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.loading<Tasks>(noEmptyTasks()),
                Event.loading<Tasks>(noEmptyTasks()).updateData(localTasks),
                Event.loading<Tasks>(noEmptyTasks()).updateData(Tasks.asSynced(remoteTasks, TEST_TIME)),
                Event.idle<Tasks>(noEmptyTasks()).updateData(Tasks.asSynced(remoteTasks, TEST_TIME))
        ))
    }

    @Test
    fun given_TheLocalDataIsEmptyAndRemoteFails_on_GetTasksEvents_it_ShouldReturnError() {
        val testObserver = TestObserver<Event<Tasks>>()
        val throwable = Throwable()
        taskRemoteDataSubject.onError(throwable)
        taskLocalDataSubject.onCompleted()

        service.getTasksEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.loading<Tasks>(noEmptyTasks()),
                Event.error<Tasks>(throwable).toBuilder().dataValidator(noEmptyTasks()).build()
        ))
    }

    @Test
    fun given_TheLocalDataIsEmptyAndRemoteIsEmpty_on_GetTasksEvents_it_ShouldReturnEmpty() {
        val testObserver = TestObserver<Event<Tasks>>()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()

        service.getTasksEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.loading<Tasks>(noEmptyTasks()),
                Event.idle<Tasks>(noEmptyTasks())
        ))
    }

    @Test
    fun given_TheLocalDataFailsAndRemoteIsEmpty_on_GetTasksEvents_it_ShouldReturnError() {
        val testObserver = TestObserver<Event<Tasks>>()
        val throwable = Throwable()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onError(throwable)

        service.getTasksEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.loading<Tasks>(noEmptyTasks()),
                Event.error<Tasks>(throwable).toBuilder().dataValidator(noEmptyTasks()).build()
        ))
    }

    @Test
    fun given_TheLocalDataFailsAndRemoteHasData_on_GetTasksEvents_it_ShouldReturnError() {
        val tasks = sampleRemoteTasks()
        val testObserver = TestObserver<Event<Tasks>>()
        val throwable = Throwable()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onError(throwable)

        service.getTasksEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.loading<Tasks>(noEmptyTasks()),
                Event.error<Tasks>(throwable).toBuilder().dataValidator(noEmptyTasks()).build()
        ))
    }

    @Test
    fun given_TheLocalDataHasDataAndRemoteFails_on_GetTasksEvents_it_ShouldReturnErrorWithData() {
        val localTasks = sampleLocalTasks()
        val testObserver = TestObserver<Event<Tasks>>()
        val throwable = Throwable()
        taskRemoteDataSubject.onError(throwable)
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()

        service.getTasksEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.loading<Tasks>(noEmptyTasks()),
                Event.loading<Tasks>(noEmptyTasks()).updateData(localTasks),
                Event.error<Tasks>(throwable).updateData(localTasks).toBuilder().dataValidator(noEmptyTasks()).build()
        ))
    }

    @Test
    fun given_remoteDataSourceDataHasChanged_on_RefreshTasks_it_ShouldReturnNewData() {
        val tasks = sampleRemoteTasks()
        val tasksRefreshed = sampleRefreshedTasks()
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()
        service.getTasks().subscribe(testObserver)
        setUpService()
        taskRemoteDataSubject.onNext(tasksRefreshed)
        taskRemoteDataSubject.onCompleted()

        service.refreshTasks().call()

        testObserver.assertReceivedOnNext(listOf(Tasks.asSynced(tasks, TEST_TIME), Tasks.asSynced(tasksRefreshed, TEST_TIME)))
    }

    @Test
    fun given_remoteDataSourceDataHasNotChanged_on_RefreshTasks_it_ShouldReturnNoAdditionalData() {
        val tasks = sampleRemoteTasks()
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()
        service.getTasks().subscribe(testObserver)
        setUpService()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()

        service.refreshTasks().call()

        testObserver.assertReceivedOnNext(listOf(Tasks.asSynced(tasks, TEST_TIME)))
    }

    @Test
    fun given_remoteDataSourceDataHasChanged_on_RefreshTasks_it_ShouldPersistNewDataToLocalDatasitory() {
        val tasks = sampleRemoteTasks()
        val tasksRefreshed = sampleRefreshedTasks()
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()
        service.getTasks().subscribe(testObserver)
        setUpService()
        taskRemoteDataSubject.onNext(tasksRefreshed)
        taskRemoteDataSubject.onCompleted()

        service.refreshTasks().call()

        verify(localDataSource).saveTasks(Tasks.asSynced(tasksRefreshed, TEST_TIME))
    }

    @Test
    fun given_ServiceHasAlreadySentData_on_RefreshTasks_it_ShouldRestartLoading() {
        val tasks = sampleRemoteTasks()
        val tasksRefreshed = sampleRefreshedTasks()
        val testObserver = TestObserver<Event<Tasks>>()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()
        service.getTasksEvents().subscribe(testObserver)
        setUpService()
        taskRemoteDataSubject.onNext(tasksRefreshed)
        taskRemoteDataSubject.onCompleted()

        service.refreshTasks().call()

        testObserver.assertReceivedOnNext(listOf(
                Event.loading<Tasks>(noEmptyTasks()),
                Event.loading<Tasks>(noEmptyTasks()).updateData(Tasks.asSynced(tasks, TEST_TIME)),
                Event.idle<Tasks>(noEmptyTasks()).updateData(Tasks.asSynced(tasks, TEST_TIME)),
                Event.loading<Tasks>(noEmptyTasks()).updateData(Tasks.asSynced(tasks, TEST_TIME)),
                Event.loading<Tasks>(noEmptyTasks()).updateData(Tasks.asSynced(tasksRefreshed, TEST_TIME)),
                Event.idle<Tasks>(noEmptyTasks()).updateData(Tasks.asSynced(tasksRefreshed, TEST_TIME))
        ))
    }

    @Test
    fun given_RemoteIsAcceptingUpdates_on_CompleteTask_it_ShouldSendAheadThenInSyncInfo() {
        val localTasks = sampleLocalTasks()
        val task = localTasks.all().get(0).data()
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321);
        service.getTasks().subscribe(testObserver)

        service.complete(task).call()

        testObserver.assertReceivedOnNext(listOf(
                localTasks,
                localTasks.save(SyncedData.from(task.complete(), SyncState.AHEAD, 321)),
                localTasks.save(SyncedData.from(task.complete(), SyncState.IN_SYNC, 321))
        ))
        assertTrue(testObserver.onCompletedEvents.isEmpty(), "Service stream should never terminate")
    }

    @Test
    fun given_ServiceHasPendingActionMoreRecentThanCurrentOne_on_CompleteTask_it_ShouldSkipTheUpdatesForCurrentAction() {
        val tasksOld = sampleLocalTasks()
        val taskOld = tasksOld.all().get(0)
        val syncedTask = SyncedData.from(taskOld.data(), taskOld.syncState(), 456)
        val tasks = tasksOld.save(syncedTask)
        val task = syncedTask.data()
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(tasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(tasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321);
        service.getTasks().subscribe(testObserver)

        service.complete(task).call()

        testObserver.assertReceivedOnNext(listOf(
                tasks
        ))
        assertTrue(testObserver.onCompletedEvents.isEmpty(), "Service stream should never terminate")
    }

    @Test
    fun given_RemoteIsFailingUpdates_on_CompleteTask_it_ShouldSendAheadThenThenMarkAsError() {
        val localTasks = sampleLocalTasks()
        val task = localTasks.all().get(0).data()
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321);
        Mockito.doAnswer {
            Observable.error<Task>(Throwable())
        }.`when`(remoteDataSource).saveTask(any())
        service.getTasks().subscribe(testObserver)

        service.complete(task).call()

        testObserver.assertReceivedOnNext(listOf(
                localTasks,
                localTasks.save(SyncedData.from(task.complete(), SyncState.AHEAD, 321)),
                localTasks.save(SyncedData.from(task.complete(), SyncState.SYNC_ERROR, 321))
        ))
        assertTrue(testObserver.onCompletedEvents.isEmpty(), "Service stream should never terminate")
    }

    @Test
    fun given_RemoteIsAcceptingUpdates_on_ActivateTask_it_ShouldSendAheadThenInSyncInfo() {
        val localTasks = sampleLocalCompletedTasks()
        val task = localTasks.all().get(0).data()
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321);
        service.getTasks().subscribe(testObserver)

        service.activate(task).call()

        testObserver.assertReceivedOnNext(listOf(
                localTasks,
                localTasks.save(SyncedData.from(task.activate(), SyncState.AHEAD, 321)),
                localTasks.save(SyncedData.from(task.activate(), SyncState.IN_SYNC, 321))
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
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(tasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(tasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321);
        service.getTasks().subscribe(testObserver)

        service.activate(task).call()

        testObserver.assertReceivedOnNext(listOf(
                tasks
        ))
        assertTrue(testObserver.onCompletedEvents.isEmpty(), "Service stream should never terminate")
    }

    @Test
    fun given_RemoteIsFailingUpdates_on_ActivateTask_it_ShouldSendAheadThenMarkAsError() {
        val localTasks = sampleLocalCompletedTasks()
        val task = localTasks.all().get(0).data()
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321);
        Mockito.doAnswer {
            Observable.error<Task>(Throwable())
        }.`when`(remoteDataSource).saveTask(any())
        service.getTasks().subscribe(testObserver)

        service.activate(task).call()

        testObserver.assertReceivedOnNext(listOf(
                localTasks,
                localTasks.save(SyncedData.from(task.activate(), SyncState.AHEAD, 321)),
                localTasks.save(SyncedData.from(task.activate(), SyncState.SYNC_ERROR, 321))
        ))
        assertTrue(testObserver.onCompletedEvents.isEmpty(), "Service stream should never terminate")
    }

    @Test
    fun given_RemoteIsAcceptingUpdates_on_SaveTask_it_ShouldSendAheadThenInSyncInfo() {
        val localTasks = sampleLocalCompletedTasks()
        val task = localTasks.all().get(0).data()
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321);
        service.getTasks().subscribe(testObserver)

        val newTask = task.toBuilder().description("NewDesc").build()
        service.save(newTask).call()

        testObserver.assertReceivedOnNext(listOf(
                localTasks,
                localTasks.save(SyncedData.from(newTask, SyncState.AHEAD, 321)),
                localTasks.save(SyncedData.from(newTask, SyncState.IN_SYNC, 321))
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
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(tasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(tasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321);
        service.getTasks().subscribe(testObserver)

        val newTask = task.toBuilder().description("NewDesc").build()
        service.save(newTask).call()

        testObserver.assertReceivedOnNext(listOf(
                tasks
        ))
        assertTrue(testObserver.onCompletedEvents.isEmpty(), "Service stream should never terminate")
    }

    @Test
    fun given_RemoteIsFailingUpdates_on_SaveTask_it_ShouldSendAheadThenMarkAsError() {
        val localTasks = sampleLocalCompletedTasks()
        val task = localTasks.all().get(0).data()
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)
        `when`(clock.timeInMillis()).thenReturn(321);
        Mockito.doAnswer {
            Observable.error<Task>(Throwable())
        }.`when`(remoteDataSource).saveTask(any())
        service.getTasks().subscribe(testObserver)

        val newTask = task.toBuilder().description("NewDesc").build()
        service.save(newTask).call()

        testObserver.assertReceivedOnNext(listOf(
                localTasks,
                localTasks.save(SyncedData.from(newTask, SyncState.AHEAD, 321)),
                localTasks.save(SyncedData.from(newTask, SyncState.SYNC_ERROR, 321))
        ))
        assertTrue(testObserver.onCompletedEvents.isEmpty(), "Service stream should never terminate")
    }

    @Test
    fun given_TheLocalDataIsEmpty_on_GetActiveTasks_it_ShouldReturnTasksFromTheRemote() {
        val tasks = sampleRemoteTasks()
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()

        service.getActiveTasks().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(Tasks.asSynced(tasks, TEST_TIME)))
        assertTrue(testObserver.onCompletedEvents.isEmpty(), "Service stream should never terminate")
    }

    @Test
    fun given_TheLocalDataIsEmpty_on_GetActiveTasks_it_ShouldSaveTasksFromTheRemoteInTheLocalData() {
        val tasks = sampleRemoteTasks()
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()

        service.getActiveTasks().subscribe(testObserver)

        verify(localDataSource).saveTasks(Tasks.asSynced(tasks, TEST_TIME))
    }

    @Test
    fun given_TheLocalDataHasTasksAndTasksAreFresh_on_GetActiveTasks_it_ShouldReturnTasksFromTheLocalData() {
        val remoteTasks = sampleRemoteTasks()
        val localTasks = sampleLocalTasks()
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onNext(remoteTasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)

        service.getActiveTasks().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(localTasks))
    }

    @Test
    fun given_TheLocalDataHasTasksAndTasksAreExpired_on_GetActiveTasks_it_ShouldReturnTasksFromTheLocalDataThenTasksFromTheRemote() {
        val remoteTasks = sampleRemoteTasks()
        val localTasks = sampleLocalTasks()
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onNext(remoteTasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(false)

        service.getActiveTasks().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(localTasks, Tasks.asSynced(remoteTasks, TEST_TIME)))
    }

    @Test
    fun given_TheLocalDataHasTasksAndTasksAreExpired_on_GetActiveTasks_it_ShouldSaveTasksFromTheRemoteInTheLocalData() {
        val remoteTasks = sampleRemoteTasks()
        val localTasks = sampleLocalTasks()
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onNext(remoteTasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(false)

        service.getActiveTasks().subscribe(testObserver)

        verify(localDataSource).saveTasks(Tasks.asSynced(remoteTasks, TEST_TIME))
    }

    @Test
    fun given_TheLocalDataHasTasksAndRemoteFails_on_GetActiveTasks_it_ShouldReturnTasksFromTheLocalData() {
        val localTasks = sampleLocalTasks()
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onError(Throwable())
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(false)

        service.getActiveTasks().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(localTasks))
    }

    @Test
    fun given_TheLocalDataIsEmptyAndRemoteFails_on_GetActiveTasks_it_ShouldReturnNothing() {
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onError(Throwable())
        taskLocalDataSubject.onCompleted()

        service.getActiveTasks().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf())
    }

    @Test
    fun given_TheLocalDataIsEmptyAndRemoteIsEmpty_on_GetActiveTasks_it_ShouldReturnNothing() {
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()

        service.getActiveTasks().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf())
    }

    @Test
    fun given_TheLocalDataFailsAndRemoteIsEmpty_on_GetActiveTasks_it_ShouldReturnNothing() {
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onError(Throwable())

        service.getActiveTasks().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf())
    }

    @Test
    fun given_TheLocalDataFailsAndRemoteHasTasks_on_GetActiveTasks_it_ShouldReturnNothing() {
        val tasks = sampleRemoteTasks()
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onError(Throwable())

        service.getActiveTasks().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf())
    }

    @Test
    fun given_TheLocalDataIsEmpty_on_GetActiveTasksEvents_it_ShouldReturnTasksFromTheRemote() {
        val tasks = sampleRemoteTasks()
        val testObserver = TestObserver<Event<Tasks>>()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()

        service.getActiveTasksEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.loading<Tasks>(noEmptyTasks()),
                Event.loading<Tasks>(noEmptyTasks()).updateData(Tasks.asSynced(tasks, TEST_TIME)),
                Event.idle<Tasks>(noEmptyTasks()).updateData(Tasks.asSynced(tasks, TEST_TIME))
        ))
    }

    @Test
    fun given_TheLocalDataHasTasksAndTasksAreFresh_on_GetActiveTasksEvents_it_ShouldReturnTasksFromTheLocalData() {
        val remoteTasks = sampleRemoteTasks()
        val localTasks = sampleLocalTasks()
        val testObserver = TestObserver<Event<Tasks>>()
        taskRemoteDataSubject.onNext(remoteTasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)

        service.getActiveTasksEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.loading<Tasks>(noEmptyTasks()),
                Event.loading<Tasks>(noEmptyTasks()).updateData(localTasks),
                Event.idle<Tasks>(noEmptyTasks()).updateData(localTasks)
        ))
    }

    @Test
    fun given_TheLocalDataHasTasksAndTasksAreExpired_on_GetActiveTasksEvents_it_ShouldReturnTasksFromTheLocalDataThenTasksFromTheRemote() {
        val remoteTasks = sampleRemoteTasks()
        val localTasks = sampleLocalTasks()
        val testObserver = TestObserver<Event<Tasks>>()
        taskRemoteDataSubject.onNext(remoteTasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(false)

        service.getActiveTasksEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.loading<Tasks>(noEmptyTasks()),
                Event.loading<Tasks>(noEmptyTasks()).updateData(localTasks),
                Event.loading<Tasks>(noEmptyTasks()).updateData(Tasks.asSynced(remoteTasks, TEST_TIME)),
                Event.idle<Tasks>(noEmptyTasks()).updateData(Tasks.asSynced(remoteTasks, TEST_TIME))
        ))
    }

    @Test
    fun given_TheLocalDataIsEmptyAndRemoteFails_on_GetActiveTasksEvents_it_ShouldReturnError() {
        val testObserver = TestObserver<Event<Tasks>>()
        val throwable = Throwable()
        taskRemoteDataSubject.onError(throwable)
        taskLocalDataSubject.onCompleted()

        service.getActiveTasksEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.loading<Tasks>(noEmptyTasks()),
                Event.error<Tasks>(throwable).toBuilder().dataValidator(noEmptyTasks()).build()
        ))
    }

    @Test
    fun given_TheLocalDataIsEmptyAndRemoteIsEmpty_on_GetActiveTasksEvents_it_ShouldReturnEmpty() {
        val testObserver = TestObserver<Event<Tasks>>()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()

        service.getActiveTasksEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.loading<Tasks>(noEmptyTasks()),
                Event.idle<Tasks>(noEmptyTasks())
        ))
    }

    @Test
    fun given_TheLocalDataFailsAndRemoteIsEmpty_on_GetActiveTasksEvents_it_ShouldReturnError() {
        val testObserver = TestObserver<Event<Tasks>>()
        val throwable = Throwable()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onError(throwable)

        service.getActiveTasksEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.loading<Tasks>(noEmptyTasks()),
                Event.error<Tasks>(throwable).toBuilder().dataValidator(noEmptyTasks()).build()
        ))
    }

    @Test
    fun given_TheLocalDataFailsAndRemoteHasData_on_GetActiveTasksEvents_it_ShouldReturnError() {
        val tasks = sampleRemoteTasks()
        val testObserver = TestObserver<Event<Tasks>>()
        val throwable = Throwable()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onError(throwable)

        service.getActiveTasksEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.loading<Tasks>(noEmptyTasks()),
                Event.error<Tasks>(throwable).toBuilder().dataValidator(noEmptyTasks()).build()
        ))
    }

    @Test
    fun given_TheLocalDataHasDataAndRemoteFails_on_GetActiveTasksEvents_it_ShouldReturnErrorWithData() {
        val localTasks = sampleLocalTasks()
        val testObserver = TestObserver<Event<Tasks>>()
        val throwable = Throwable()
        taskRemoteDataSubject.onError(throwable)
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()

        service.getActiveTasksEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.loading<Tasks>(noEmptyTasks()),
                Event.loading<Tasks>(noEmptyTasks()).updateData(localTasks),
                Event.error<Tasks>(throwable).updateData(localTasks).toBuilder().dataValidator(noEmptyTasks()).build()
        ))
    }

    @Test
    fun given_TheTasksAreAllCompleted_on_GetActiveTasksEvents_it_ShouldReturnEmpty() {
        val tasks = sampleLocalCompletedTasks()
        val testObserver = TestObserver<Event<Tasks>>()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(tasks)
        taskLocalDataSubject.onCompleted()

        service.getActiveTasksEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.loading<Tasks>(noEmptyTasks()),
                Event.loading<Tasks>(noEmptyTasks()),
                Event.idle<Tasks>(noEmptyTasks())
        ))
    }

    @Test
    fun given_TheTasksSomeTasksAreCompleted_on_GetActiveTasksEvents_it_ShouldFilterTasks() {
        val tasks = sampleLocalSomeCompletedTasks()
        val testObserver = TestObserver<Event<Tasks>>()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(tasks)
        taskLocalDataSubject.onCompleted()

        service.getActiveTasksEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.loading<Tasks>(noEmptyTasks()),
                Event.loading<Tasks>(noEmptyTasks()).updateData(sampleLocalActivatedTasks()),
                Event.idle<Tasks>(noEmptyTasks()).updateData(sampleLocalActivatedTasks())
        ))
    }

    @Test
    fun given_TheLocalDataIsEmpty_on_GetCompletedTasks_it_ShouldReturnTasksFromTheRemote() {
        val tasks = sampleRemoteCompletedTasks()
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()

        service.getCompletedTasks().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(Tasks.asSynced(tasks, TEST_TIME)))
        assertTrue(testObserver.onCompletedEvents.isEmpty(), "Service stream should never terminate")
    }

    @Test
    fun given_TheLocalDataIsEmpty_on_GetCompletedTasks_it_ShouldSaveTasksFromTheRemoteInTheLocalData() {
        val tasks = sampleRemoteCompletedTasks()
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()

        service.getCompletedTasks().subscribe(testObserver)

        verify(localDataSource).saveTasks(Tasks.asSynced(tasks, TEST_TIME))
    }

    @Test
    fun given_TheLocalDataHasTasksAndTasksAreFresh_on_GetCompletedTasks_it_ShouldReturnTasksFromTheLocalData() {
        val remoteTasks = sampleRemoteCompletedTasks()
        val localTasks = sampleLocalCompletedTasks()
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onNext(remoteTasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)

        service.getCompletedTasks().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(localTasks))
    }

    @Test
    fun given_TheLocalDataHasTasksAndTasksAreExpired_on_GetCompletedTasks_it_ShouldReturnTasksFromTheLocalDataThenTasksFromTheRemote() {
        val remoteTasks = sampleRemoteCompletedTasks()
        val localTasks = sampleLocalCompletedTasks()
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onNext(remoteTasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(false)

        service.getCompletedTasks().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(localTasks, Tasks.asSynced(remoteTasks, TEST_TIME)))
    }

    @Test
    fun given_TheLocalDataHasTasksAndTasksAreExpired_on_GetCompletedTasks_it_ShouldSaveTasksFromTheRemoteInTheLocalData() {
        val remoteTasks = sampleRemoteCompletedTasks()
        val localTasks = sampleLocalCompletedTasks()
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onNext(remoteTasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(false)

        service.getCompletedTasks().subscribe(testObserver)

        verify(localDataSource).saveTasks(Tasks.asSynced(remoteTasks, TEST_TIME))
    }

    @Test
    fun given_TheLocalDataHasTasksAndRemoteFails_on_GetCompletedTasks_it_ShouldReturnTasksFromTheLocalData() {
        val localTasks = sampleLocalCompletedTasks()
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onError(Throwable())
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(false)

        service.getCompletedTasks().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(localTasks))
    }

    @Test
    fun given_TheLocalDataIsEmptyAndRemoteFails_on_GetCompletedTasks_it_ShouldReturnNothing() {
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onError(Throwable())
        taskLocalDataSubject.onCompleted()

        service.getCompletedTasks().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf())
    }

    @Test
    fun given_TheLocalDataIsEmptyAndRemoteIsEmpty_on_GetCompletedTasks_it_ShouldReturnNothing() {
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()

        service.getCompletedTasks().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf())
    }

    @Test
    fun given_TheLocalDataFailsAndRemoteIsEmpty_on_GetCompletedTasks_it_ShouldReturnNothing() {
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onError(Throwable())

        service.getCompletedTasks().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf())
    }

    @Test
    fun given_TheLocalDataFailsAndRemoteHasTasks_on_GetCompletedTasks_it_ShouldReturnNothing() {
        val tasks = sampleRemoteCompletedTasks()
        val testObserver = TestObserver<Tasks>()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onError(Throwable())

        service.getCompletedTasks().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf())
    }

    @Test
    fun given_TheLocalDataIsEmpty_on_GetCompletedTasksEvents_it_ShouldReturnTasksFromTheRemote() {
        val tasks = sampleRemoteCompletedTasks()
        val testObserver = TestObserver<Event<Tasks>>()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()

        service.getCompletedTasksEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.loading<Tasks>(noEmptyTasks()),
                Event.loading<Tasks>(noEmptyTasks()).updateData(Tasks.asSynced(tasks, TEST_TIME)),
                Event.idle<Tasks>(noEmptyTasks()).updateData(Tasks.asSynced(tasks, TEST_TIME))
        ))
    }

    @Test
    fun given_TheLocalDataHasTasksAndTasksAreFresh_on_GetCompletedTasksEvents_it_ShouldReturnTasksFromTheLocalData() {
        val remoteTasks = sampleRemoteCompletedTasks()
        val localTasks = sampleLocalCompletedTasks()
        val testObserver = TestObserver<Event<Tasks>>()
        taskRemoteDataSubject.onNext(remoteTasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(true)

        service.getCompletedTasksEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.loading<Tasks>(noEmptyTasks()),
                Event.loading<Tasks>(noEmptyTasks()).updateData(localTasks),
                Event.idle<Tasks>(noEmptyTasks()).updateData(localTasks)
        ))
    }

    @Test
    fun given_TheLocalDataHasTasksAndTasksAreExpired_on_GetCompletedTasksEvents_it_ShouldReturnTasksFromTheLocalDataThenTasksFromTheRemote() {
        val remoteTasks = sampleRemoteCompletedTasks()
        val localTasks = sampleLocalCompletedTasks()
        val testObserver = TestObserver<Event<Tasks>>()
        taskRemoteDataSubject.onNext(remoteTasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()
        `when`(freshnessChecker.isFresh(localTasks)).thenReturn(false)

        service.getCompletedTasksEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.loading<Tasks>(noEmptyTasks()),
                Event.loading<Tasks>(noEmptyTasks()).updateData(localTasks),
                Event.loading<Tasks>(noEmptyTasks()).updateData(Tasks.asSynced(remoteTasks, TEST_TIME)),
                Event.idle<Tasks>(noEmptyTasks()).updateData(Tasks.asSynced(remoteTasks, TEST_TIME))
        ))
    }

    @Test
    fun given_TheLocalDataIsEmptyAndRemoteFails_on_GetCompletedTasksEvents_it_ShouldReturnError() {
        val testObserver = TestObserver<Event<Tasks>>()
        val throwable = Throwable()
        taskRemoteDataSubject.onError(throwable)
        taskLocalDataSubject.onCompleted()

        service.getCompletedTasksEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.loading<Tasks>(noEmptyTasks()),
                Event.error<Tasks>(throwable).toBuilder().dataValidator(noEmptyTasks()).build()
        ))
    }

    @Test
    fun given_TheLocalDataIsEmptyAndRemoteIsEmpty_on_GetCompletedTasksEvents_it_ShouldReturnEmpty() {
        val testObserver = TestObserver<Event<Tasks>>()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()

        service.getCompletedTasksEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.loading<Tasks>(noEmptyTasks()),
                Event.idle<Tasks>(noEmptyTasks())
        ))
    }

    @Test
    fun given_TheLocalDataFailsAndRemoteIsEmpty_on_GetCompletedTasksEvents_it_ShouldReturnError() {
        val testObserver = TestObserver<Event<Tasks>>()
        val throwable = Throwable()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onError(throwable)

        service.getCompletedTasksEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.loading<Tasks>(noEmptyTasks()),
                Event.error<Tasks>(throwable).toBuilder().dataValidator(noEmptyTasks()).build()
        ))
    }

    @Test
    fun given_TheLocalDataFailsAndRemoteHasData_on_GetCompletedTasksEvents_it_ShouldReturnError() {
        val tasks = sampleRemoteCompletedTasks()
        val testObserver = TestObserver<Event<Tasks>>()
        val throwable = Throwable()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onError(throwable)

        service.getCompletedTasksEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.loading<Tasks>(noEmptyTasks()),
                Event.error<Tasks>(throwable).toBuilder().dataValidator(noEmptyTasks()).build()
        ))
    }

    @Test
    fun given_TheLocalDataHasDataAndRemoteFails_on_GetCompletedTasksEvents_it_ShouldReturnErrorWithData() {
        val localTasks = sampleLocalCompletedTasks()
        val testObserver = TestObserver<Event<Tasks>>()
        val throwable = Throwable()
        taskRemoteDataSubject.onError(throwable)
        taskLocalDataSubject.onNext(localTasks)
        taskLocalDataSubject.onCompleted()

        service.getCompletedTasksEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.loading<Tasks>(noEmptyTasks()),
                Event.loading<Tasks>(noEmptyTasks()).updateData(localTasks),
                Event.error<Tasks>(throwable).updateData(localTasks).toBuilder().dataValidator(noEmptyTasks()).build()
        ))
    }

    @Test
    fun given_TheTasksAreAllActivated_on_GetCompletedTasksEvents_it_ShouldReturnEmpty() {
        val tasks = sampleLocalActivatedTasks()
        val testObserver = TestObserver<Event<Tasks>>()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(tasks)
        taskLocalDataSubject.onCompleted()

        service.getCompletedTasksEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.loading<Tasks>(noEmptyTasks()),
                Event.loading<Tasks>(noEmptyTasks()),
                Event.idle<Tasks>(noEmptyTasks())
        ))
    }

    @Test
    fun given_TheTasksSomeTasksAreCompleted_on_GetCompletedTasksEvents_it_ShouldFilterTasks() {
        val tasks = sampleLocalSomeCompletedTasks()
        val testObserver = TestObserver<Event<Tasks>>()
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onNext(tasks)
        taskLocalDataSubject.onCompleted()

        service.getCompletedTasksEvents().subscribe(testObserver)

        testObserver.assertReceivedOnNext(listOf(
                Event.loading<Tasks>(noEmptyTasks()),
                Event.loading<Tasks>(noEmptyTasks()).updateData(sampleLocalCompletedTasks()),
                Event.idle<Tasks>(noEmptyTasks()).updateData(sampleLocalCompletedTasks())
        ))
    }

    @Test
    fun given_WeHaveTasksInTheService_on_ClearCompletedTasks_it_ShouldReturnLocallyClearedTaskFirstThenConfirm() {
        val tasks = sampleRemoteSomeCompletedTasks()
        val testObserver = TestObserver<Event<Tasks>>()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()
        service.getTasksEvents().subscribe(TestObserver<Event<Tasks>>())
        `when`(remoteDataSource.clearCompletedTasks()).thenReturn(Observable.just(sampleRemoteSomeCompletedTasksDeleted()));
        service.getTasksEvents().subscribe(testObserver)

        service.clearCompletedTasks().call();

        testObserver.assertReceivedOnNext(listOf(
                Event.idle<Tasks>(noEmptyTasks()).updateData(Tasks.asSynced(tasks, TEST_TIME)),
                Event.loading<Tasks>(noEmptyTasks()).updateData(sampleLocalSomeCompletedTasksDeleted()),
                Event.loading<Tasks>(noEmptyTasks()).updateData(Tasks.asSynced(sampleRemoteSomeCompletedTasksDeleted(), TEST_TIME)),
                Event.idle<Tasks>(noEmptyTasks()).updateData(Tasks.asSynced(sampleRemoteSomeCompletedTasksDeleted(), TEST_TIME))
                ))
    }

    @Test
    fun given_RemoteSourceFailsToClearCompletedTasks_on_ClearCompletedTasks_it_ShouldReturnLocallyClearedTaskFirstThenSyncError() {
        val tasks = sampleRemoteSomeCompletedTasks()
        val testObserver = TestObserver<Event<Tasks>>()
        taskRemoteDataSubject.onNext(tasks)
        taskRemoteDataSubject.onCompleted()
        taskLocalDataSubject.onCompleted()
        service.getTasksEvents().subscribe(TestObserver<Event<Tasks>>())
        `when`(remoteDataSource.clearCompletedTasks()).thenReturn(Observable.error(Throwable("Terrible things")));
        service.getTasksEvents().subscribe(testObserver)

        service.clearCompletedTasks().call();

        testObserver.assertReceivedOnNext(listOf(
                Event.idle<Tasks>(noEmptyTasks()).updateData(Tasks.asSynced(tasks, TEST_TIME)),
                Event.loading<Tasks>(noEmptyTasks()).updateData(sampleLocalSomeCompletedTasksDeleted()),
                Event.idle<Tasks>(noEmptyTasks()).updateData(sampleSomeCompletedTasksDeletedFailed()).asError(SyncError())
        ))
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

    private fun sampleLocalTasks() = Tasks.asSynced(listOf(
            Task.builder().id(Id.from("24")).title("Bar").build()
    ), TEST_TIME)

    private fun sampleLocalCompletedTasks() = Tasks.asSynced(listOf(
            Task.builder().id(Id.from("42")).title("Foo").isCompleted(true).build()
    ), TEST_TIME)

    private fun sampleLocalActivatedTasks() = Tasks.asSynced(listOf(
            Task.builder().id(Id.from("24")).title("Bar").build()
    ), TEST_TIME)

    private fun sampleLocalSomeCompletedTasks() = Tasks.asSynced(listOf(
            Task.builder().id(Id.from("42")).title("Foo").isCompleted(true).build(),
            Task.builder().id(Id.from("24")).title("Bar").build()
    ), TEST_TIME)

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
