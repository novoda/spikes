package com.novoda.todoapp.task.newtask.presenter

import com.google.common.base.Optional
import com.novoda.todoapp.navigation.Navigator
import com.novoda.todoapp.task.TaskActionDisplayer
import com.novoda.todoapp.task.data.model.Id
import com.novoda.todoapp.task.data.model.Task
import com.novoda.todoapp.task.presenter.IdProducer
import com.novoda.todoapp.tasks.service.TasksService
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers
import org.mockito.Mockito
import org.mockito.Mockito.never

class NewTaskPresenterTest {

    val TASK_ID = Id.from("TEST_ID")!!

    var service: TasksService = Mockito.mock(TasksService::class.java)

    var taskDisplayer: TaskActionDisplayer = Mockito.mock(TaskActionDisplayer::class.java)
    var navigator: Navigator = Mockito.mock(Navigator::class.java)

    var idGenerator = Mockito.mock(IdProducer::class.java)

    var presenter = NewTaskPresenter(service, taskDisplayer, navigator, idGenerator)

    @Before
    fun setUp() {
        setUpService()
        presenter = NewTaskPresenter(service, taskDisplayer, navigator, idGenerator)
    }

    @After
    fun tearDown() {
        Mockito.reset(taskDisplayer, service)
    }

    @Test
    fun `Given the presenter is not presenting, On start presenting, It should attach the action listener to the displayer`() {
        givenThePresenterIsNotPresenting()

        presenter.startPresenting()

        Mockito.verify(taskDisplayer).attach(presenter.taskActionListener)
    }

    @Test
    fun `Given the presenter is presenting, On stop presenting, It should detach the action listener from the displayer`() {
        givenThePresenterIsPresenting()

        presenter.stopPresenting()

        Mockito.verify(taskDisplayer).detach(presenter.taskActionListener)
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
    fun `Given task title is valid and no description, On save task, It should generate an id`() {
        givenThePresenterIsPresenting()
        val task = Task.builder()
                .id(TASK_ID)
                .title("Title")
                .build()

        presenter.taskActionListener.save(task.title(), task.description())

        Mockito.verify(idGenerator).produce()
    }

    @Test
    fun `Given task description is valid and no title, On save task, It should generate an id`() {
        givenThePresenterIsPresenting()
        val task = Task.builder()
                .id(TASK_ID)
                .description("Description")
                .build()

        presenter.taskActionListener.save(task.title(), task.description())

        Mockito.verify(idGenerator).produce()
    }

    @Test
    fun `Given task title and description are missing, On save task, It should not generate an id`() {
        givenThePresenterIsPresenting()

        presenter.taskActionListener.save(Optional.absent(), Optional.absent())

        Mockito.verify(idGenerator, never()).produce()
    }

    @Test
    fun `Given task title and description are missing, On save task, It should present empty task error`() {
        givenThePresenterIsPresenting()

        presenter.taskActionListener.save(Optional.absent(), Optional.absent())

        Mockito.verify(taskDisplayer).showEmptyTaskError()
    }

    private fun givenThePresenterIsPresenting() {
        presenter.startPresenting()
    }

    private fun givenThePresenterIsNotPresenting() {
    }

    private fun setUpService() {
        Mockito.`when`(idGenerator.produce()).thenReturn(TASK_ID)
    }
}


