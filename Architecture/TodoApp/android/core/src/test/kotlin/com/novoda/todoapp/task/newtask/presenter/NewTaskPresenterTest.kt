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
    fun given_ThePresenterIsNotPresenting_on_StartPresenting_it_ShouldAttachListenerToTheView() {
        givenThePresenterIsNotPresenting()

        presenter.startPresenting()

        Mockito.verify(taskDisplayer).attach(presenter.taskActionListener)
    }

    @Test
    fun given_ThePresenterIsPresenting_on_StopPresenting_it_ShouldDetachListenerFromTheView() {
        givenThePresenterIsPresenting()

        presenter.stopPresenting()

        Mockito.verify(taskDisplayer).detach(presenter.taskActionListener)
    }

    @Test
    fun given_TaskTitleAndDescriptionAreValid_on_SaveTask_it_ShouldSaveTaskToService() {
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
    fun given_TaskTitleIsValid_on_SaveTask_it_ShouldSaveTaskToService() {
        givenThePresenterIsPresenting()
        val task = Task.builder()
                .id(TASK_ID)
                .title("Title")
                .build()

        presenter.taskActionListener.save(task.title(), task.description())

        Mockito.verify(service).save(task)
    }

    @Test
    fun given_TaskDescriptionIsValid_on_SaveTask_it_ShouldSaveTaskToService() {
        givenThePresenterIsPresenting()
        val task = Task.builder()
                .id(TASK_ID)
                .description("Description")
                .build()

        presenter.taskActionListener.save(task.title(), task.description())

        Mockito.verify(service).save(task)
    }

    @Test
    fun given_TaskTitleAndDescriptionAreInvalid_on_SaveTask_it_ShouldNotSaveTaskToService() {
        givenThePresenterIsPresenting()

        presenter.taskActionListener.save(Optional.absent(), Optional.absent())

        Mockito.verify(service, never()).save(Matchers.any())
    }

    @Test
    fun given_TaskDescriptionIsValid_on_SaveTask_it_ShouldNavigateBack() {
        givenThePresenterIsPresenting()
        val task = Task.builder()
                .id(TASK_ID)
                .description("Description")
                .build()

        presenter.taskActionListener.save(task.title(), task.description())

        Mockito.verify(navigator).back()
    }

    @Test
    fun given_TaskTitleAndDescriptionAreInvalid_on_SaveTask_it_ShouldNotNavigateBack() {
        givenThePresenterIsPresenting()

        presenter.taskActionListener.save(Optional.absent(), Optional.absent())

        Mockito.verify(navigator, never()).back()
    }

    @Test
    fun given_TaskTitleIsValid_on_SaveTask_it_ShouldNavigateBack() {
        givenThePresenterIsPresenting()
        val task = Task.builder()
                .id(TASK_ID)
                .title("Title")
                .build()

        presenter.taskActionListener.save(task.title(), task.description())

        Mockito.verify(navigator).back()
    }

    @Test
    fun given_TaskTitleIsValid_on_SaveTask_it_ShouldGenerateId() {
        givenThePresenterIsPresenting()
        val task = Task.builder()
                .id(TASK_ID)
                .title("Title")
                .build()

        presenter.taskActionListener.save(task.title(), task.description())

        Mockito.verify(idGenerator).produce()
    }

    @Test
    fun given_TaskDescriptionIsValid_on_SaveTask_it_ShouldGenerateId() {
        givenThePresenterIsPresenting()
        val task = Task.builder()
                .id(TASK_ID)
                .description("Description")
                .build()

        presenter.taskActionListener.save(task.title(), task.description())

        Mockito.verify(idGenerator).produce()
    }

    @Test
    fun given_TaskTitleAndDescriptionAreInvalid_on_SaveTask_it_ShouldNotGenerateId() {
        givenThePresenterIsPresenting()

        presenter.taskActionListener.save(Optional.absent(), Optional.absent())

        Mockito.verify(idGenerator, never()).produce()
    }

    @Test
    fun given_TaskTitleAndDescriptionAreInvalid_on_SaveTask_it_ShouldPresentEmptyTaskError() {
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


