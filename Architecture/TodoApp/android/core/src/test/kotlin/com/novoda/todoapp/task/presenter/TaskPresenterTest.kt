package com.novoda.todoapp.task.presenter;

import com.novoda.data.SyncState
import com.novoda.data.SyncedData
import com.novoda.todoapp.task.data.model.Id
import com.novoda.todoapp.task.data.model.Task
import com.novoda.todoapp.task.displayer.TaskDisplayer
import com.novoda.todoapp.tasks.service.TasksService
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers
import org.mockito.Mockito
import rx.functions.Action0
import rx.subjects.BehaviorSubject
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TaskPresenterTest {

    val TASK_ID = Id.from("TEST_ID")

    var taskSubject: BehaviorSubject<SyncedData<Task>> = BehaviorSubject.create()
    var service: TasksService = Mockito.mock(TasksService::class.java)

    var displayer: TaskDisplayer = Mockito.mock(TaskDisplayer::class.java)

    var completeAction: Action0 = Mockito.mock(Action0::class.java)
    var activateAction: Action0 = Mockito.mock(Action0::class.java)

    var presenter = TaskPresenter(TASK_ID, service, displayer)

    @Before
    fun setUp() {
        setUpService()
        presenter = TaskPresenter(TASK_ID, service, displayer)
    }

    @After
    fun tearDown() {
        Mockito.reset(displayer, service)
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfNewTask_it_ShouldPresentTheTaskToTheView() {
        givenThePresenterIsPresenting()

        taskSubject.onNext(simpleSyncedTask())

        Mockito.verify(displayer).display(simpleSyncedTask())
    }

    @Test
    fun given_ThePresenterStoppedPresenting_on_EmissionOfNewTask_it_ShouldNotPresentTheTaskToTheView() {
        givenThePresenterStoppedPresenting()

        taskSubject.onNext(simpleSyncedTask())

        Mockito.verifyZeroInteractions(displayer)
    }

    @Test
    fun given_ThePresenterIsNotPresenting_on_StartPresenting_it_ShouldAttachListenerToTheView() {
        givenThePresenterIsNotPresenting()

        presenter.startPresenting()

        Mockito.verify(displayer).attach(presenter.taskActionListener)
    }

    @Test
    fun given_ThePresenterIsPresenting_on_StopPresenting_it_ShouldDetachListenerFromTheView() {
        givenThePresenterIsPresenting()

        presenter.stopPresenting()

        Mockito.verify(displayer).detach(presenter.taskActionListener)
    }

    @Test
    fun given_ThePresenterIsNotPresenting_on_StartPresenting_it_ShouldSubscribeToTheTaskStream() {
        givenThePresenterIsNotPresenting()

        presenter.startPresenting()

        assertTrue(taskSubject.hasObservers())
    }

    @Test
    fun given_ThePresenterIsPresenting_on_StopPresenting_it_ShouldUnsubscribeFromTheTasksStream() {
        givenThePresenterIsPresenting()

        presenter.stopPresenting()

        assertFalse(taskSubject.hasObservers())
    }

    @Test
    fun given_ThePresenterIsPresenting_on_ToggleCompleteSelectedForCompletedTask_it_ShouldReactivateTask() {
        givenThePresenterIsPresenting()
        val completedTask = simpleTask().complete()

        presenter.taskActionListener.toggleCompletion(completedTask)

        Mockito.verify(service).activate(completedTask)
        Mockito.verify(activateAction).call()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_ToggleCompleteSelectedForActivatedTask_it_ShouldCompleteTask() {
        givenThePresenterIsPresenting()
        val activatedTask = simpleTask().activate()

        presenter.taskActionListener.toggleCompletion(activatedTask)

        Mockito.verify(service).complete(activatedTask)
        Mockito.verify(completeAction).call()
    }

    private fun givenThePresenterIsPresenting() {
        presenter.startPresenting()
    }

    private fun givenThePresenterIsNotPresenting() {
    }

    private fun givenThePresenterStoppedPresenting() {
        presenter.startPresenting()
        presenter.stopPresenting()
        Mockito.reset(displayer)
    }

    private fun simpleSyncedTask() = SyncedData.from(simpleTask(), SyncState.IN_SYNC, 123)

    private fun simpleTask() = Task.builder()
            .id(TASK_ID)
            .title("Foo")
            .build()

    private fun setUpService() {
        taskSubject = BehaviorSubject.create()
        Mockito.`when`(service.getTask(TASK_ID)).thenReturn(taskSubject)
        Mockito.`when`(service.complete(Matchers.any())).thenReturn(completeAction)
        Mockito.`when`(service.activate(Matchers.any())).thenReturn(activateAction)
    }
}

