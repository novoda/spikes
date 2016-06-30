package com.novoda.todoapp.task.edit.presenter;

import com.google.common.base.Optional
import com.novoda.data.SyncState
import com.novoda.data.SyncedData
import com.novoda.todoapp.navigation.Navigator
import com.novoda.todoapp.task.data.model.Id
import com.novoda.todoapp.task.data.model.Task
import com.novoda.todoapp.task.edit.displayer.TaskEditDisplayer
import com.novoda.todoapp.tasks.service.TasksService
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers
import org.mockito.Mockito
import org.mockito.Mockito.never
import rx.functions.Action0
import rx.subjects.BehaviorSubject
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EditTaskPresenterTest {

    val TASK_ID = Id.from("TEST_ID")

    var taskSubject: BehaviorSubject<SyncedData<Task>> = BehaviorSubject.create()
    var service: TasksService = Mockito.mock(TasksService::class.java)

    var displayer: TaskEditDisplayer = Mockito.mock(TaskEditDisplayer::class.java)
    var navigator: Navigator = Mockito.mock(Navigator::class.java)

    var saveAction: Action0 = Mockito.mock(Action0::class.java)

    var presenter = EditTaskPresenter(TASK_ID, service, displayer, navigator)

    @Before
    fun setUp() {
        setUpService()
        presenter = EditTaskPresenter(TASK_ID, service, displayer, navigator)
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

        Mockito.verify(displayer).attach(presenter.taskEditActionListener)
    }

    @Test
    fun given_ThePresenterIsPresenting_on_StopPresenting_it_ShouldDetachListenerFromTheView() {
        givenThePresenterIsPresenting()

        presenter.stopPresenting()

        Mockito.verify(displayer).detach(presenter.taskEditActionListener)
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
    fun given_TaskTitleAndDescriptionAreValid_on_SaveTask_it_ShouldSaveTaskToService() {
        givenThePresenterIsPresenting()
        val task = Task.builder()
                        .id(TASK_ID)
                        .title("Title")
                        .description("Description")
                        .build()

        presenter.taskEditActionListener.save(task.title(), task.description());

        Mockito.verify(service).save(task)
        Mockito.verify(saveAction).call()
    }

    @Test
    fun given_TaskTitleIsValid_on_SaveTask_it_ShouldSaveTaskToService() {
        givenThePresenterIsPresenting()
        val task = Task.builder()
                .id(TASK_ID)
                .title("Title")
                .build()

        presenter.taskEditActionListener.save(task.title(), task.description());

        Mockito.verify(service).save(task)
        Mockito.verify(saveAction).call()
    }

    @Test
    fun given_TaskDescriptionIsValid_on_SaveTask_it_ShouldSaveTaskToService() {
        givenThePresenterIsPresenting()
        val task = Task.builder()
                .id(TASK_ID)
                .description("Description")
                .build()

        presenter.taskEditActionListener.save(task.title(), task.description());

        Mockito.verify(service).save(task)
        Mockito.verify(saveAction).call()
    }

    @Test
    fun given_TaskTitleAndDescriptionAreInvalid_on_SaveTask_it_ShouldNotSaveTaskToService() {
        givenThePresenterIsPresenting()

        presenter.taskEditActionListener.save(Optional.absent(), Optional.absent());

        Mockito.verify(service, never()).save(Matchers.any())
        Mockito.verify(saveAction, never()).call()
    }

    @Test
    fun given_TaskTitleIsValid_on_SaveTask_it_ShouldNavigateBack() {
        givenThePresenterIsPresenting()
        val task = Task.builder()
                .id(TASK_ID)
                .title("Title")
                .build()

        presenter.taskEditActionListener.save(task.title(), task.description());

        Mockito.verify(navigator).back()
    }

    @Test
    fun given_TaskDescriptionIsValid_on_SaveTask_it_ShouldNavigateBack() {
        givenThePresenterIsPresenting()
        val task = Task.builder()
                .id(TASK_ID)
                .description("Description")
                .build()

        presenter.taskEditActionListener.save(task.title(), task.description());

        Mockito.verify(navigator).back()
    }

    @Test
    fun given_TaskTitleAndDescriptionAreInvalid_on_SaveTask_it_ShouldNotNavigateBack() {
        givenThePresenterIsPresenting()

        presenter.taskEditActionListener.save(Optional.absent(), Optional.absent());

        Mockito.verify(navigator, never()).back()
    }

    @Test
    fun given_TaskTitleAndDescriptionAreInvalid_on_SaveTask_it_ShouldPresentEmptyTaskError() {
        givenThePresenterIsPresenting()

        presenter.taskEditActionListener.save(Optional.absent(), Optional.absent());

        Mockito.verify(displayer).showEmptyTaskError()
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
        Mockito.`when`(service.save(Matchers.any())).thenReturn(saveAction)
    }
}


