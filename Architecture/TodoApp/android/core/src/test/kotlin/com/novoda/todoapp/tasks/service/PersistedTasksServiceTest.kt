package com.novoda.todoapp.tasks.service

import com.novoda.data.SyncState
import com.novoda.data.SyncedData
import com.novoda.event.Event
import com.novoda.todoapp.task.data.model.Id
import com.novoda.todoapp.task.data.model.Task
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
    fun given_RemoteIsFailingUpdates_on_CompleteTask_it_ShouldSendAheadThenRevertToActivatedInSyncInfo() {
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
                localTasks.save(SyncedData.from(task, SyncState.IN_SYNC, 321))
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
    fun given_RemoteIsFailingUpdates_on_ActivateTask_it_ShouldSendAheadThenRevertToCompletedInSyncInfo() {
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
                localTasks.save(SyncedData.from(task, SyncState.IN_SYNC, 321))
        ))
        assertTrue(testObserver.onCompletedEvents.isEmpty(), "Service stream should never terminate")
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

    private fun sampleLocalTasks() = Tasks.asSynced(listOf(
            Task.builder().id(Id.from("24")).title("Bar").build()
    ), TEST_TIME)

    private fun sampleLocalCompletedTasks() = Tasks.asSynced(listOf(
            Task.builder().id(Id.from("24")).title("Bar").isCompleted(true).build()
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
