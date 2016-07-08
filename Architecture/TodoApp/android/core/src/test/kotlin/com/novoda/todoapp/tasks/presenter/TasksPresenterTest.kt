package com.novoda.todoapp.tasks.presenter

import com.google.common.collect.ImmutableList
import com.novoda.data.SyncState
import com.novoda.data.SyncedData
import com.novoda.event.Event
import com.novoda.todoapp.navigation.NavDrawerDisplayer
import com.novoda.todoapp.navigation.Navigator
import com.novoda.todoapp.task.data.model.Id
import com.novoda.todoapp.task.data.model.Task
import com.novoda.todoapp.tasks.NoEmptyTasksPredicate
import com.novoda.todoapp.tasks.data.model.Tasks
import com.novoda.todoapp.tasks.displayer.TasksActionListener
import com.novoda.todoapp.tasks.displayer.TasksDisplayer
import com.novoda.todoapp.tasks.loading.displayer.TasksLoadingDisplayer
import com.novoda.todoapp.tasks.service.SyncError
import com.novoda.todoapp.tasks.service.TasksService
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers
import org.mockito.Mockito
import org.mockito.Mockito.*
import rx.functions.Action0
import rx.subjects.BehaviorSubject
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TasksPresenterTest {

    val TEST_TIME = 123L

    var tasksEventSubject: BehaviorSubject<Event<Tasks>> = BehaviorSubject.create()

    var tasksCompletedEventSubject: BehaviorSubject<Event<Tasks>> = BehaviorSubject.create()

    var tasksActiveEventSubject: BehaviorSubject<Event<Tasks>> = BehaviorSubject.create()

    var service: TasksService = Mockito.mock(TasksService::class.java)

    var displayer: TasksDisplayer = Mockito.mock(TasksDisplayer::class.java)
    var loadingDisplayer: TasksLoadingDisplayer = Mockito.mock(TasksLoadingDisplayer::class.java)
    var navDrawerDisplayer: NavDrawerDisplayer = Mockito.mock(NavDrawerDisplayer::class.java)
    var navigator: Navigator = Mockito.mock(Navigator::class.java)

    var refreshAction: Action0 = Mockito.mock(Action0::class.java)
    var completeAction: Action0 = Mockito.mock(Action0::class.java)
    var activateAction: Action0 = Mockito.mock(Action0::class.java)
    var clearCompletedAction: Action0 = Mockito.mock(Action0::class.java)

    var presenter = TasksPresenter(service, displayer, loadingDisplayer, navDrawerDisplayer, navigator)

    @Before
    fun setUp() {
        setUpService()
        presenter = TasksPresenter(service, displayer, loadingDisplayer, navDrawerDisplayer, navigator)
    }

    @After
    fun tearDown() {
        Mockito.reset(displayer, loadingDisplayer, service)
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfNewTasks_it_ShouldPresentTheTasksToTheView() {
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(nonEmptyIdleEventWith(simpleTasks()))

        Mockito.verify(displayer).display(simpleTasks())
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfSomeNewDeletedLocallyTasks_it_ShouldPresentNonDeletedTasksToTheView() {
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(nonEmptyIdleEventWith(someDeletedLocallyTasks()))

        Mockito.verify(displayer).display(nonDeletedTasksOnly())
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfAllDeletedLocallyTasks_it_ShouldNotPresentToTheView() {
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(nonEmptyIdleEventWith(allDeletedLocallyTasks()))

        Mockito.verify(displayer, never()).display(any(Tasks::class.java))
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfALoadingEventWithNoData_it_ShouldPresentTheLoadingScreen() {
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(emptyLoadingEvent())

        Mockito.verify(loadingDisplayer).showLoadingScreen()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfALoadingEventWithData_it_ShouldPresentTheLoadingIndicator() {
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(defaultLoadingEventWith(simpleTasks()))

        Mockito.verify(loadingDisplayer).showLoadingIndicator()
    }


    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfLoadingEventWithSomeDeletedData_it_ShouldPresentLoadingIndicator() {
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(defaultLoadingEventWith(someDeletedLocallyTasks()))

        Mockito.verify(loadingDisplayer).showLoadingIndicator()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfLoadingEventWithAllDeletedData_it_ShouldPresentLoadingScreen() {
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(nonEmptyLoadingEventWith(allDeletedLocallyTasks()))

        Mockito.verify(loadingDisplayer).showLoadingScreen()
    }

    @Test
    fun given_TheCurrentFilterIsAll_on_EmissionOfAnIdleEventWithNoData_it_ShouldPresentTheEmptyScreen() {
        presenter.setInitialFilterTo(TasksActionListener.Filter.ALL)
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(emptyIdleEvent())

        Mockito.verify(loadingDisplayer).showEmptyTasksScreen()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfIdleEventWithSomeDeletedData_it_ShouldPresentData() {
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(defaultIdleEventWith(someDeletedLocallyTasks()))

        Mockito.verify(loadingDisplayer).showData()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfIdleEventWithAllDeletedData_it_ShouldPresentEmptyTasksScreen() {
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(nonEmptyIdleEventWith(allDeletedLocallyTasks()))

        Mockito.verify(loadingDisplayer).showEmptyTasksScreen()
    }

    @Test
    fun given_TheCurrentFilterIsActive_on_EmissionOfAnIdleEventWithNoData_it_ShouldPresentTheEmptyActiveTasksScreen() {
        presenter.setInitialFilterTo(TasksActionListener.Filter.ACTIVE)
        givenThePresenterIsPresenting()

        tasksActiveEventSubject.onNext(emptyIdleEvent())

        Mockito.verify(loadingDisplayer).showEmptyActiveTasksScreen()
    }

    @Test
    fun given_TheCurrentFilterIsCompleted_on_EmissionOfAnIdleEventWithNoData_it_ShouldPresentTheEmptyCompletedTasksScreen() {
        presenter.setInitialFilterTo(TasksActionListener.Filter.COMPLETED)
        givenThePresenterIsPresenting()

        tasksCompletedEventSubject.onNext(emptyIdleEvent())

        Mockito.verify(loadingDisplayer).showEmptyCompletedTasksScreen()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfAnIdleEventWithData_it_ShouldPresentData() {
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(defaultIdleEventWith(simpleTasks()))

        Mockito.verify(loadingDisplayer).showData()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfAnErrorEventWithNoData_it_ShouldPresentTheErrorScreen() {
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(errorEvent())

        Mockito.verify(loadingDisplayer).showErrorScreen()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfErrorEventWithSomeDeletedData_it_ShouldPresentErrorIndicator() {
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(errorEventWith(someDeletedLocallyTasks()))

        Mockito.verify(loadingDisplayer).showErrorIndicator()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfErrorEventWithAllDeletedData_it_ShouldPresentErrorScreen() {
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(errorEventWith(allDeletedLocallyTasks()))

        Mockito.verify(loadingDisplayer).showErrorScreen()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_EmissionOfAnErrorEventWithData_it_ShouldPresentTheErrorIndicator() {
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(errorEventWith(simpleTasks()))

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

        tasksEventSubject.onNext(nonEmptyIdleEventWith(simpleTasks()))

        Mockito.verifyZeroInteractions(displayer)
    }

    @Test
    fun given_ThePresenterStoppedPresenting_on_EmissionOfAnEvent_it_ShouldNotPresentToTheLoadingView() {
        givenThePresenterStoppedPresenting()

        tasksEventSubject.onNext(defaultLoadingEvent())

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

        assertTrue(tasksEventSubject.hasObservers())
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

        assertFalse(tasksEventSubject.hasObservers())
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

        presenter.tasksActionListener.onTaskSelected(simpleTask)

        Mockito.verify(navigator).toTaskDetail(simpleTask)
    }

    @Test
    fun given_ThePresenterIsPresenting_on_RefreshSelected_it_ShouldRefreshTheService() {
        givenThePresenterIsPresenting()

        presenter.tasksActionListener.onRefreshSelected()

        Mockito.verify(service).refreshTasks()
        Mockito.verify(refreshAction).call()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_ClearCompletedSelected_it_ShouldClearCompletedOnTheService() {
        givenThePresenterIsPresenting()

        presenter.tasksActionListener.onClearCompletedSelected()

        Mockito.verify(service).clearCompletedTasks()
        Mockito.verify(clearCompletedAction).call()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_AddTaskSelected_it_ShouldNavigateToAddTask() {
        givenThePresenterIsPresenting()

        presenter.tasksActionListener.onAddTaskSelected()

        Mockito.verify(navigator).toAddTask()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_AllFilterSelected_it_ShouldReSubscribeToTasksEventStream() {
        givenThePresenterIsPresenting()

        presenter.tasksActionListener.onFilterSelected(TasksActionListener.Filter.ALL)

        Mockito.verify(service, times(4)).tasksEvent
        assertTrue(tasksEventSubject.hasObservers())
    }

    @Test
    fun given_ThePresenterIsPresenting_on_ActiveFilterSelected_it_ShouldSubscribeToActiveTasksEventStream() {
        givenThePresenterIsPresenting()

        presenter.tasksActionListener.onFilterSelected(TasksActionListener.Filter.ACTIVE)

        Mockito.verify(service, times(2)).activeTasksEvent
        assertTrue(tasksActiveEventSubject.hasObservers())
    }

    @Test
    fun given_ThePresenterIsPresenting_on_CompletedFilterSelected_it_ShouldSubscribeToCompletedTasksEventStream() {
        givenThePresenterIsPresenting()

        presenter.tasksActionListener.onFilterSelected(TasksActionListener.Filter.COMPLETED)

        Mockito.verify(service, times(2)).completedTasksEvent
        assertTrue(tasksCompletedEventSubject.hasObservers())
    }

    @Test
    fun given_ThePresenterIsPresenting_on_CompletedFilterSelected_it_ShouldUpdateCurrentFilter() {
        givenThePresenterIsPresenting()

        presenter.tasksActionListener.onFilterSelected(TasksActionListener.Filter.COMPLETED)

        assertEquals(TasksActionListener.Filter.COMPLETED, presenter.currentFilter, "Expected current filter to be updated")
    }

    @Test
    fun given_ThePresenterIsPresenting_on_ActiveFilterSelected_it_ShouldUpdateCurrentFilter() {
        givenThePresenterIsPresenting()

        presenter.tasksActionListener.onFilterSelected(TasksActionListener.Filter.ACTIVE)

        assertEquals(TasksActionListener.Filter.ACTIVE, presenter.currentFilter, "Expected current filter to be updated")
    }

    @Test
    fun given_ThePresenterIsSetWithInitialFilter_on_StartPresenting_it_ShouldSubscribeToTheCorrespondingEventStream() {
        givenThePresenterIsNotPresenting()
        presenter.setInitialFilterTo(TasksActionListener.Filter.ACTIVE)

        presenter.startPresenting()

        Mockito.verify(service, times(2)).activeTasksEvent
        assertTrue(tasksActiveEventSubject.hasObservers())
    }

    @Test
    fun given_ThePresenterIsNotPresenting_on_StartPresenting_it_ShouldAttachTheNavDrawerActionListener_ToTheDisplayer() {
        givenThePresenterIsNotPresenting()

        presenter.startPresenting()

        Mockito.verify(navDrawerDisplayer).attach(presenter.navDrawerActionListener)
    }

    @Test
    fun given_ThePresenterIsPresenting_on_StatisticsNavDrawerItemSelected_it_ShouldNavigateToStatistics() {
        givenThePresenterIsPresenting()

        presenter.navDrawerActionListener.onStatisticsNavDrawerItemSelected()

        Mockito.verify(navigator).toStatistics()
    }

    @Test
    fun given_ThePresenterIsPresenting_on_ToDoListNavDrawerItemSelected_it_ShouldCloseTheNavDrawer() {
        givenThePresenterIsPresenting()

        presenter.navDrawerActionListener.onToDoListNavDrawerItemSelected()

        Mockito.verify(navDrawerDisplayer).closeNavDrawer()
    }

    private fun defaultIdleEvent() = Event.idle<Tasks>()

    private fun defaultIdleEventWith(tasks: Tasks) = defaultIdleEvent().updateData(tasks)

    private fun emptyIdleEvent() = Event.idle(noEmptyTasks())

    private fun nonEmptyIdleEventWith(tasks: Tasks) = emptyIdleEvent().updateData(tasks)

    private fun defaultLoadingEvent() = Event.loading<Tasks>()

    private fun defaultLoadingEventWith(tasks: Tasks) = defaultLoadingEvent().updateData(tasks)

    private fun emptyLoadingEvent() = Event.loading(noEmptyTasks())

    private fun nonEmptyLoadingEventWith(tasks: Tasks) = emptyLoadingEvent().updateData(tasks)

    private fun errorEventWith(tasks: Tasks) = errorEvent().updateData(tasks)

    private fun errorEvent() = emptyIdleEvent().asError(SyncError())

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

    private fun someDeletedLocallyTasks() = Tasks.from(ImmutableList.copyOf(listOf(
            SyncedData.from(Task.builder().id(Id.from("24")).title("Bar").isCompleted(true).build(), SyncState.DELETED_LOCALLY, TEST_TIME),
            SyncedData.from(Task.builder().id(Id.from("42")).title("Foo").build(), SyncState.AHEAD, TEST_TIME),
            SyncedData.from(Task.builder().id(Id.from("12")).title("Whizz").build(), SyncState.IN_SYNC, TEST_TIME),
            SyncedData.from(Task.builder().id(Id.from("424")).title("New").isCompleted(false).build(), SyncState.DELETED_LOCALLY, TEST_TIME)
    )))

    private fun nonDeletedTasksOnly() = Tasks.from(ImmutableList.copyOf(listOf(
            SyncedData.from(Task.builder().id(Id.from("42")).title("Foo").build(), SyncState.AHEAD, TEST_TIME),
            SyncedData.from(Task.builder().id(Id.from("12")).title("Whizz").build(), SyncState.IN_SYNC, TEST_TIME)
    )))

    private fun allDeletedLocallyTasks() = Tasks.from(ImmutableList.copyOf(listOf(
            SyncedData.from(Task.builder().id(Id.from("24")).title("Bar").isCompleted(true).build(), SyncState.DELETED_LOCALLY, TEST_TIME),
            SyncedData.from(Task.builder().id(Id.from("42")).title("Foo").build(), SyncState.DELETED_LOCALLY, TEST_TIME),
            SyncedData.from(Task.builder().id(Id.from("12")).title("Whizz").build(), SyncState.DELETED_LOCALLY, TEST_TIME),
            SyncedData.from(Task.builder().id(Id.from("424")).title("New").isCompleted(false).build(), SyncState.DELETED_LOCALLY, TEST_TIME)
    )))

    private fun noEmptyTasks() = NoEmptyTasksPredicate()

    private fun setUpService() {
        tasksEventSubject = BehaviorSubject.create()

        Mockito.`when`(service.tasksEvent).thenReturn(tasksEventSubject)

        Mockito.`when`(service.activeTasksEvent).thenReturn(tasksActiveEventSubject)

        Mockito.`when`(service.completedTasksEvent).thenReturn(tasksCompletedEventSubject)

        Mockito.`when`(service.refreshTasks()).thenReturn(refreshAction)
        Mockito.`when`(service.clearCompletedTasks()).thenReturn(clearCompletedAction)
        Mockito.`when`(service.complete(Matchers.any())).thenReturn(completeAction)
        Mockito.`when`(service.activate(Matchers.any())).thenReturn(activateAction)
    }
}
