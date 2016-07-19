package com.novoda.todoapp.task.edit.presenter;

import com.google.common.base.Optional
import com.novoda.data.SyncState
import com.novoda.data.SyncedData
import com.novoda.todoapp.navigation.Navigator
import com.novoda.todoapp.task.data.model.Id
import com.novoda.todoapp.task.data.model.Task
import com.novoda.todoapp.task.edit.displayer.EditTaskDisplayer
import com.novoda.todoapp.tasks.service.TasksService
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers
import org.mockito.Mockito
import org.mockito.Mockito.never
import rx.subjects.BehaviorSubject
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EditTaskPresenterTest {

    val TASK_ID = Id.from("TEST_ID")!!
    val TASK_ID2 = Id.from("TEST_ID2")!!

    var taskSubject: BehaviorSubject<SyncedData<Task>> = BehaviorSubject.create()
    var service: TasksService = Mockito.mock(TasksService::class.java)

    var taskDisplayer: EditTaskDisplayer = Mockito.mock(EditTaskDisplayer::class.java)
    var navigator: Navigator = Mockito.mock(Navigator::class.java)

    var presenter = EditTaskPresenter(service, taskDisplayer, navigator, TASK_ID)

    @Before
    fun setUp() {
        setUpService()
        presenter = EditTaskPresenter(service, taskDisplayer, navigator, TASK_ID)
    }

    @After
    fun tearDown() {
        Mockito.reset(taskDisplayer, service)
    }

    @Test
    fun `Given the presenter is presenting, On emission of a new task, It should present the task to the displayer`() {
        givenThePresenterIsPresenting()

        taskSubject.onNext(simpleSyncedTask())

        Mockito.verify(taskDisplayer).display(simpleSyncedTask())
    }

    @Test
    fun `Given the presenter already has received a task, On emission of a new task, It should not present the task to the displayer`() {
        givenThePresenterIsPresenting()
        taskSubject.onNext(simpleSyncedTask())

        taskSubject.onNext(simpleSyncedTask2())

        Mockito.verify(taskDisplayer, never()).display(simpleSyncedTask2())
    }

    @Test
    fun `Given the presenter stopped presenting, On emission of a new task, It should not present anything to the displayer`() {
        givenThePresenterStoppedPresenting()

        taskSubject.onNext(simpleSyncedTask())

        Mockito.verifyZeroInteractions(taskDisplayer)
    }

    @Test
    fun `Given the presenter is not presenting, On startPresenting, It should attach the action listener to the displayer`() {
        givenThePresenterIsNotPresenting()

        presenter.startPresenting()

        Mockito.verify(taskDisplayer).attach(presenter.taskActionListener)
    }

    @Test
    fun `Given the presenter is presenting, On stopPresenting, It should detach the action listener from the displayer`() {
        givenThePresenterIsPresenting()

        presenter.stopPresenting()

        Mockito.verify(taskDisplayer).detach(presenter.taskActionListener)
    }

    @Test
    fun `Given the presenter is not presenting, On startPresenting, It should subscribe to the task stream`() {
        givenThePresenterIsNotPresenting()

        presenter.startPresenting()

        assertTrue(taskSubject.hasObservers())
    }

    @Test
    fun `Given the presenter is presenting, On stopPresenting, It should unsubscribe from the task stream`() {
        givenThePresenterIsPresenting()

        presenter.stopPresenting()

        assertFalse(taskSubject.hasObservers())
    }

    @Test
    fun `Given task title and description are valid, On save task, It should save task to the service`() {
        givenThePresenterIsPresenting()
        val task = Task.builder()
                .id(TASK_ID)
                .title("Title")
                .description("Description")
                .build()

        presenter.taskActionListener.save(task.title(), task.description())

        Mockito.verify(service).save(task)
    }

    @Test
    fun `Given task title is valid and no description, On save task, It should save task to the service`() {
        givenThePresenterIsPresenting()
        val task = Task.builder()
                .id(TASK_ID)
                .title("Title")
                .build()

        presenter.taskActionListener.save(task.title(), task.description())

        Mockito.verify(service).save(task)
    }

    @Test
    fun `Given task description is valid and no title, On save task, It should save task to the service`() {
        givenThePresenterIsPresenting()
        val task = Task.builder()
                .id(TASK_ID)
                .description("Description")
                .build()

        presenter.taskActionListener.save(task.title(), task.description())

        Mockito.verify(service).save(task)
    }

    @Test
    fun `Given task title and description are missing, On save task, It should not save task to the service`() {
        givenThePresenterIsPresenting()

        presenter.taskActionListener.save(Optional.absent(), Optional.absent())

        Mockito.verify(service, never()).save(Matchers.any())
    }

    @Test
    fun `Given task title is valid and no description, On save task, It should navigate back`() {
        givenThePresenterIsPresenting()
        val task = Task.builder()
                .id(TASK_ID)
                .title("Title")
                .build()

        presenter.taskActionListener.save(task.title(), task.description())

        Mockito.verify(navigator).back()
    }

    @Test
    fun `Given task description is valid and no title, On save task, It should navigate back`() {
        givenThePresenterIsPresenting()
        val task = Task.builder()
                .id(TASK_ID)
                .description("Description")
                .build()

        presenter.taskActionListener.save(task.title(), task.description())

        Mockito.verify(navigator).back()
    }

    @Test
    fun `Given task title and description are missing, On save task, It should not navigate back`() {
        givenThePresenterIsPresenting()

        presenter.taskActionListener.save(Optional.absent(), Optional.absent())

        Mockito.verify(navigator, never()).back()
    }

    @Test
    fun `Given task title and description are missing, On save task, It should present empty task error`() {
        givenThePresenterIsPresenting()

        presenter.taskActionListener.save(Optional.absent(), Optional.absent())

        Mockito.verify(taskDisplayer).showEmptyTaskError()
    }

    @Test
    fun `Given the presenter is presenting, On up selected, It should navigate back`() {
        givenThePresenterIsPresenting()

        presenter.taskActionListener.onUpSelected()

        Mockito.verify(navigator).back()
    }

    private fun givenThePresenterIsPresenting() {
        presenter.startPresenting()
    }

    private fun givenThePresenterIsNotPresenting() {
    }

    private fun givenThePresenterStoppedPresenting() {
        presenter.startPresenting()
        presenter.stopPresenting()
        Mockito.reset(taskDisplayer)
    }

    private fun simpleSyncedTask() = SyncedData.from(simpleTask(), SyncState.IN_SYNC, 123)

    private fun simpleSyncedTask2() = SyncedData.from(simpleTask2(), SyncState.IN_SYNC, 321)

    private fun simpleTask() = Task.builder()
            .id(TASK_ID)
            .title("Foo")
            .build()

    private fun simpleTask2() = Task.builder()
            .id(TASK_ID2)
            .title("Bar")
            .build()

    private fun setUpService() {
        taskSubject = BehaviorSubject.create()
        Mockito.`when`(service.getTask(TASK_ID)).thenReturn(taskSubject)
    }
}


