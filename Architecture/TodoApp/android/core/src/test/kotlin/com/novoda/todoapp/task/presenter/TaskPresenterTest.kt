package com.novoda.todoapp.task.presenter

import com.novoda.data.SyncState
import com.novoda.data.SyncedData
import com.novoda.todoapp.navigation.Navigator
import com.novoda.todoapp.task.data.model.Id
import com.novoda.todoapp.task.data.model.Task
import com.novoda.todoapp.task.displayer.TaskDisplayer
import com.novoda.todoapp.tasks.service.TasksService
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import rx.subjects.BehaviorSubject
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TaskPresenterTest {

    val TASK_ID = Id.from("TEST_ID")!!

    var taskSubject: BehaviorSubject<SyncedData<Task>> = BehaviorSubject.create()
    var service: TasksService = Mockito.mock(TasksService::class.java)

    var displayer: TaskDisplayer = Mockito.mock(TaskDisplayer::class.java)
    var navigator: Navigator = Mockito.mock(Navigator::class.java)

    var presenter = TaskPresenter(TASK_ID, service, displayer, navigator)

    @Before
    fun setUp() {
        setUpService()
        presenter = TaskPresenter(TASK_ID, service, displayer, navigator)
    }

    @After
    fun tearDown() {
        Mockito.reset(displayer, service)
    }

    @Test
    fun `Given the presenter is presenting, On emission of a new task, It should present the task to the displayer`() {
        givenThePresenterIsPresenting()

        taskSubject.onNext(simpleSyncedTask())

        Mockito.verify(displayer).display(simpleSyncedTask())
    }

    @Test
    fun `Given the presenter stopped presenting, On emission of a new task, It should not present anything to the displayer`() {
        givenThePresenterStoppedPresenting()

        taskSubject.onNext(simpleSyncedTask())

        Mockito.verifyZeroInteractions(displayer)
    }

    @Test
    fun `Given the presenter is not presenting, On startPresenting, It should attach the action listener to the displayer`() {
        givenThePresenterIsNotPresenting()

        presenter.startPresenting()

        Mockito.verify(displayer).attach(presenter.taskActionListener)
    }

    @Test
    fun `Given the presenter is presenting, On stopPresenting, It should detach the action listener from the displayer`() {
        givenThePresenterIsPresenting()

        presenter.stopPresenting()

        Mockito.verify(displayer).detach(presenter.taskActionListener)
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
    fun `Given the presenter is presenting, On toggleCompletion selected for a completed task, It should reactivate the task`() {
        givenThePresenterIsPresenting()
        val completedTask = simpleTask().complete()

        presenter.taskActionListener.toggleCompletion(completedTask)

        Mockito.verify(service).activate(completedTask)
    }

    @Test
    fun `Given the presenter is presenting, On toggleCompletion selected for an active task, It should completed the task`() {
        givenThePresenterIsPresenting()
        val activatedTask = simpleTask().activate()

        presenter.taskActionListener.toggleCompletion(activatedTask)

        Mockito.verify(service).complete(activatedTask)
    }

    @Test
    fun `Given the presenter is presenting, On edit task selected, It should navigate to task edit`() {
        givenThePresenterIsPresenting()
        val simpleTask = simpleTask()

        presenter.taskActionListener.onEditSelected(simpleTask)

        Mockito.verify(navigator).toEditTask(simpleTask)
    }

    @Test
    fun `Given the presenter is presenting, On delete task selected, It should delete the task on the service`() {
        givenThePresenterIsPresenting()
        val simpleTask = simpleTask()

        presenter.taskActionListener.onDeleteSelected(simpleTask)

        Mockito.verify(service).delete(simpleTask)
    }

    @Test
    fun `Given the presenter is presenting, On delete task selected, It should navigate back`() {
        givenThePresenterIsPresenting()
        val simpleTask = simpleTask()

        presenter.taskActionListener.onDeleteSelected(simpleTask)

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
    }
}

