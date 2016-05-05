package com.novoda.todoapp.tasks.presenter;

import com.novoda.event.Event
import com.novoda.todoapp.loading.displayer.LoadingDisplayer
import com.novoda.todoapp.navigation.Navigator
import com.novoda.todoapp.task.data.model.Id
import com.novoda.todoapp.task.data.model.Task
import com.novoda.todoapp.tasks.data.model.Tasks
import com.novoda.todoapp.tasks.displayer.TasksActionListener
import com.novoda.todoapp.tasks.displayer.TasksDisplayer
import com.novoda.todoapp.tasks.service.TasksService
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers
import org.mockito.Mockito
import org.mockito.Mockito.times
import rx.functions.Action0
import rx.subjects.BehaviorSubject
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TasksPresenterTest {

    val TEST_TIME = 123L

    var tasksSubject: BehaviorSubject<Tasks> = BehaviorSubject.create()
    var tasksEventSubject: BehaviorSubject<Event<Tasks>> = BehaviorSubject.create()

    var tasksCompletedSubject: BehaviorSubject<Tasks> = BehaviorSubject.create()
    var tasksCompletedEventSubject: BehaviorSubject<Event<Tasks>> = BehaviorSubject.create()

    var tasksActiveSubject: BehaviorSubject<Tasks> = BehaviorSubject.create()
    var tasksActiveEventSubject: BehaviorSubject<Event<Tasks>> = BehaviorSubject.create()

    var service: TasksService = Mockito.mock(TasksService::class.java)

    var displayer: TasksDisplayer = Mockito.mock(TasksDisplayer::class.java)
    var loadingDisplayer: LoadingDisplayer = Mockito.mock(LoadingDisplayer::class.java)
    var navigator: Navigator = Mockito.mock(Navigator::class.java)

    var refreshAction: Action0 = Mockito.mock(Action0::class.java)
    var completeAction: Action0 = Mockito.mock(Action0::class.java)
    var activateAction: Action0 = Mockito.mock(Action0::class.java)
    var clearCompletedAction: Action0 = Mockito.mock(Action0::class.java)

    var presenter = TasksPresenter(service, displayer, loadingDisplayer, navigator)

    @Before
    fun setUp() {
        setUpService()
        presenter = TasksPresenter(service, displayer, loadingDisplayer, navigator)
    }

    @After
    fun tearDown() {
        Mockito.reset(displayer, loadingDisplayer, service)
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfNewTasks_it_ShouldPresentTheTasksToTheView() {
        givenThePresenterIsPresenting()

        tasksSubject.onNext(simpleTasks())

        Mockito.verify(displayer).display(simpleTasks())
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfALoadingEventWithNoData_it_ShouldPresentTheLoadingScreen() {
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(Event.loading())

        Mockito.verify(loadingDisplayer).showLoadingScreen()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfALoadingEventWithData_it_ShouldPresentTheLoadingIndicator() {
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(Event.loading<Tasks>().updateData(simpleTasks()))

        Mockito.verify(loadingDisplayer).showLoadingIndicator()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfAnIdleEventWithNoData_it_ShouldPresentTheEmptyScreen() {
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(Event.idle())

        Mockito.verify(loadingDisplayer).showEmptyScreen()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfAnIdleEventWithData_it_ShouldPresentData() {
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(Event.idle<Tasks>().updateData(simpleTasks()))

        Mockito.verify(loadingDisplayer).showData()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfAnErrorEventWithNoData_it_ShouldPresentTheErrorScreen() {
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(Event.error(Throwable()))

        Mockito.verify(loadingDisplayer).showErrorScreen()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfAnErrorEventWithData_it_ShouldPresentTheErrorIndicator() {
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(Event.error<Tasks>(Throwable()).updateData(simpleTasks()))

        Mockito.verify(loadingDisplayer).showErrorIndicator()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_RetryClicked_it_ShouldRefreshTheService() {
        givenThePresenterIsPresenting()

        presenter.retryActionListener.onRetry()

        Mockito.verify(service).refreshTasks()
        Mockito.verify(refreshAction).call()
    }

    @Test
    fun given_ThePresenterStoppedPresenting_on_EmissionOfNewTasks_it_ShouldNotPresentTheTasksToTheView() {
        givenThePresenterStoppedPresenting()

        tasksSubject.onNext(simpleTasks())

        Mockito.verifyZeroInteractions(displayer)
    }

    @Test
    fun given_ThePresenterStoppedPresenting_on_EmissionOfAnEvent_it_ShouldNotPresentToTheLoadingView() {
        givenThePresenterStoppedPresenting()

        tasksEventSubject.onNext(Event.loading())

        Mockito.verifyZeroInteractions(loadingDisplayer)
    }

    @Test
    fun given_ThePresenterIsNotPresenting_on_StartPresenting_it_ShouldAttachListenerToTheLoadingView() {
        givenThePresenterIsNotPresenting()

        presenter.startPresenting()

        Mockito.verify(loadingDisplayer).attach(presenter.retryActionListener)
    }

    @Test
    fun given_ThePresenterIsNotPresenting_on_StartPresenting_it_ShouldAttachListenerToTheView() {
        givenThePresenterIsNotPresenting()

        presenter.startPresenting()

        Mockito.verify(displayer).attach(presenter.tasksActionListener)
    }

    @Test
    fun given_ThePresenterIsPresenting_on_StopPresenting_it_ShouldDetachListenerFromTheLoadingView() {
        givenThePresenterIsPresenting()

        presenter.stopPresenting()

        Mockito.verify(loadingDisplayer).detach(presenter.retryActionListener)
    }

    @Test
    fun given_ThePresenterIsPresenting_on_StopPresenting_it_ShouldDetachListenerFromTheView() {
        givenThePresenterIsPresenting()

        presenter.stopPresenting()

        Mockito.verify(displayer).detach(presenter.tasksActionListener)
    }

    @Test
    fun given_ThePresenterIsNotPresenting_on_StartPresenting_it_ShouldSubscribeToTheTasksStream() {
        givenThePresenterIsNotPresenting()

        presenter.startPresenting()

        assertTrue(tasksSubject.hasObservers())
    }

    @Test
    fun given_ThePresenterIsNotPresenting_on_StartPresenting_it_ShouldSubscribeToTheEventStream() {
        givenThePresenterIsNotPresenting()

        presenter.startPresenting()

        assertTrue(tasksEventSubject.hasObservers())
    }

    @Test
    fun given_ThePresenterIsPresenting_on_StopPresenting_it_ShouldUnsubscribeFromTheTasksStream() {
        givenThePresenterIsPresenting()

        presenter.stopPresenting()

        assertFalse(tasksSubject.hasObservers())
    }

    @Test
    fun given_ThePresenterIsPresenting_on_StopPresenting_it_ShouldUnsubscribeFromTheEventStream() {
        givenThePresenterIsPresenting()

        presenter.stopPresenting()

        assertFalse(tasksEventSubject.hasObservers())
    }

    @Test
    fun given_ThePresenterIsPresenting_on_ToggleCompleteSelectedForCompletedTask_it_ShouldReactivateTask() {
        givenThePresenterIsPresenting()
        val completedTask = simpleTask().complete()

        presenter.tasksActionListener.toggleCompletion(completedTask)

        Mockito.verify(service).activate(completedTask)
        Mockito.verify(activateAction).call()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_ToggleCompleteSelectedForActivatedTask_it_ShouldCompleteTask() {
        givenThePresenterIsPresenting()
        val activatedTask = simpleTask().activate()

        presenter.tasksActionListener.toggleCompletion(activatedTask)

        Mockito.verify(service).complete(activatedTask)
        Mockito.verify(completeAction).call()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_TaskSelected_it_ShouldNavigateToTaskDetail() {
        givenThePresenterIsPresenting()
        val simpleTask = simpleTask()

        presenter.tasksActionListener.onTaskSelected(simpleTask);

        Mockito.verify(navigator).toTaskDetail(simpleTask)
    }

    @Test
    fun given_ThePresenterIsPresenting_on_RefreshSelected_it_ShouldRefreshTheService() {
        givenThePresenterIsPresenting()
        val simpleTask = simpleTask()

        presenter.tasksActionListener.onRefreshSelected();

        Mockito.verify(service).refreshTasks()
        Mockito.verify(refreshAction).call()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_ClearCompletedSelected_it_ShouldClearCompletedOnTheService() {
        givenThePresenterIsPresenting()
        val simpleTask = simpleTask()

        presenter.tasksActionListener.onClearCompletedSelected()

        Mockito.verify(service).clearCompletedTasks()
        Mockito.verify(clearCompletedAction).call()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_AllFilterSelected_it_ShouldReSubscribeToTasksStream() {
        givenThePresenterIsPresenting()
        val simpleTask = simpleTask()

        presenter.tasksActionListener.onFilterSelected(TasksActionListener.Filter.ALL)

        Mockito.verify(service, times(2)).getTasks()
        assertTrue(tasksSubject.hasObservers())
    }

    @Test
    fun given_ThePresenterIsPresenting_on_AllFilterSelected_it_ShouldReSubscribeToTasksEventStream() {
        givenThePresenterIsPresenting()
        val simpleTask = simpleTask()

        presenter.tasksActionListener.onFilterSelected(TasksActionListener.Filter.ALL)

        Mockito.verify(service, times(2)).getTasksEvents()
        assertTrue(tasksEventSubject.hasObservers())
    }

    @Test
    fun given_ThePresenterIsPresenting_on_ActiveFilterSelected_it_ShouldSubscribeToActiveTasksStream() {
        givenThePresenterIsPresenting()
        val simpleTask = simpleTask()

        presenter.tasksActionListener.onFilterSelected(TasksActionListener.Filter.ACTIVE)

        Mockito.verify(service).getActiveTasks()
        assertTrue(tasksActiveSubject.hasObservers())
    }

    @Test
    fun given_ThePresenterIsPresenting_on_ActiveFilterSelected_it_ShouldSubscribeToActiveTasksEventStream() {
        givenThePresenterIsPresenting()
        val simpleTask = simpleTask()

        presenter.tasksActionListener.onFilterSelected(TasksActionListener.Filter.ACTIVE)

        Mockito.verify(service).getActiveTasksEvents()
        assertTrue(tasksActiveEventSubject.hasObservers())
    }

    @Test
    fun given_ThePresenterIsPresenting_on_CompletedFilterSelected_it_ShouldSubscribeToCompletedTasksStream() {
        givenThePresenterIsPresenting()
        val simpleTask = simpleTask()

        presenter.tasksActionListener.onFilterSelected(TasksActionListener.Filter.COMPLETED)

        Mockito.verify(service).getCompletedTasks()
        assertTrue(tasksCompletedSubject.hasObservers())
    }

    @Test
    fun given_ThePresenterIsPresenting_on_CompletedFilterSelected_it_ShouldSubscribeToCompletedTasksEventStream() {
        givenThePresenterIsPresenting()
        val simpleTask = simpleTask()

        presenter.tasksActionListener.onFilterSelected(TasksActionListener.Filter.COMPLETED)

        Mockito.verify(service).getCompletedTasksEvents()
        assertTrue(tasksCompletedEventSubject.hasObservers())
    }

    @Test
    fun given_ThePresenterIsPresenting_on_CompletedFilterSelected_it_ShouldUpdateCurrentFilter() {
        givenThePresenterIsPresenting()
        val simpleTask = simpleTask()

        presenter.tasksActionListener.onFilterSelected(TasksActionListener.Filter.COMPLETED)

        assertEquals(TasksActionListener.Filter.COMPLETED, presenter.currentFilter, "Expected current filter to be updated")
    }

    @Test
    fun given_ThePresenterIsPresenting_on_ActiveFilterSelected_it_ShouldUpdateCurrentFilter() {
        givenThePresenterIsPresenting()
        val simpleTask = simpleTask()

        presenter.tasksActionListener.onFilterSelected(TasksActionListener.Filter.ACTIVE)

        assertEquals(TasksActionListener.Filter.ACTIVE, presenter.currentFilter, "Expected current filter to be updated")
    }

    @Test
    fun given_ThePresenterIsSetWithInitalFilter_on_StartPresenting_it_ShouldSubscribeToTheCorrespondingTasksStream() {
        givenThePresenterIsNotPresenting()
        presenter.setInitialFilterTo(TasksActionListener.Filter.ACTIVE)

        presenter.startPresenting()

        Mockito.verify(service).getActiveTasks()
        assertTrue(tasksActiveSubject.hasObservers())
    }

    @Test
    fun given_ThePresenterIsSetWithInitalFilter_on_StartPresenting_it_ShouldSubscribeToTheCorrespondingEventStream() {
        givenThePresenterIsNotPresenting()
        presenter.setInitialFilterTo(TasksActionListener.Filter.ACTIVE)

        presenter.startPresenting()

        Mockito.verify(service).getActiveTasksEvents()
        assertTrue(tasksActiveEventSubject.hasObservers())
    }

    private fun givenThePresenterIsPresenting() {
        presenter.startPresenting()
    }

    private fun givenThePresenterIsNotPresenting() {
    }

    private fun givenThePresenterStoppedPresenting() {
        presenter.startPresenting()
        presenter.stopPresenting()
        Mockito.reset(loadingDisplayer, displayer)
    }

    private fun simpleTasks() = Tasks.asSynced(listOf(
            simpleTask()
    ), TEST_TIME)

    private fun simpleTask() = Task.builder()
        .id(Id.from("42"))
        .title("Foo")
        .build()

    private fun setUpService() {
        tasksSubject = BehaviorSubject.create()
        tasksEventSubject = BehaviorSubject.create()

        Mockito.`when`(service.getTasks()).thenReturn(tasksSubject)
        Mockito.`when`(service.getTasksEvents()).thenReturn(tasksEventSubject)

        Mockito.`when`(service.getActiveTasks()).thenReturn(tasksActiveSubject)
        Mockito.`when`(service.getActiveTasksEvents()).thenReturn(tasksActiveEventSubject)

        Mockito.`when`(service.getCompletedTasks()).thenReturn(tasksCompletedSubject)
        Mockito.`when`(service.getCompletedTasksEvents()).thenReturn(tasksCompletedEventSubject)

        Mockito.`when`(service.refreshTasks()).thenReturn(refreshAction)
        Mockito.`when`(service.clearCompletedTasks()).thenReturn(clearCompletedAction)
        Mockito.`when`(service.complete(Matchers.any())).thenReturn(completeAction)
        Mockito.`when`(service.activate(Matchers.any())).thenReturn(activateAction)
    }
}
