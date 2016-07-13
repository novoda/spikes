package com.novoda.todoapp.tasks.presenter

import com.google.common.collect.ImmutableList
import com.novoda.data.SyncState
import com.novoda.data.SyncedData
import com.novoda.event.Event
import com.novoda.todoapp.navigation.Navigator
import com.novoda.todoapp.navigation.TopLevelMenuDisplayer
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
import org.mockito.Mockito
import org.mockito.Mockito.*
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
    var topLevelMenuDisplayer: TopLevelMenuDisplayer = Mockito.mock(TopLevelMenuDisplayer::class.java)
    var navigator: Navigator = Mockito.mock(Navigator::class.java)

    var presenter = TasksPresenter(service, displayer, loadingDisplayer, topLevelMenuDisplayer, navigator)

    @Before
    fun setUp() {
        setUpService()
        presenter = TasksPresenter(service, displayer, loadingDisplayer, topLevelMenuDisplayer, navigator)
    }

    @After
    fun tearDown() {
        Mockito.reset(displayer, loadingDisplayer, service)
    }

    @Test
    fun `Given the presenter is presenting, On emission of new tasks, It should present the tasks to the displayer`() {
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(nonEmptyIdleEventWith(simpleTasks()))

        Mockito.verify(displayer).display(simpleTasks())
    }

    @Test
    fun `Given the presenter is presenting, On emission of some new deleted locally tasks, It should present non deleted tasks to the displayer`() {
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(nonEmptyIdleEventWith(someDeletedLocallyTasks()))

        Mockito.verify(displayer).display(nonDeletedTasksOnly())
    }

    @Test
    fun `Given the presenter is presenting, On emission of all new deleted locally tasks, It should not present to the displayer`() {
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(nonEmptyIdleEventWith(allDeletedLocallyTasks()))

        Mockito.verify(displayer, never()).display(any(Tasks::class.java))
    }

    @Test
    fun `Given the presenter is presenting, On emission of an empty loading event, It should present to the loading screen`() {
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(emptyLoadingEvent())

        Mockito.verify(loadingDisplayer).showLoadingScreen()
    }

    @Test
    fun `Given the presenter is presenting, On emission of a loading event with data, It should present to the loading indicator`() {
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(defaultLoadingEventWith(simpleTasks()))

        Mockito.verify(loadingDisplayer).showLoadingIndicator()
    }


    @Test
    fun `Given the presenter is presenting, On emission of a loading event with some deleted data, It should present to the loading indicator`() {
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(defaultLoadingEventWith(someDeletedLocallyTasks()))

        Mockito.verify(loadingDisplayer).showLoadingIndicator()
    }

    @Test
    fun `Given the presenter is presenting, On emission of a loading event with all deleted data, It should present to the loading screen`() {
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(nonEmptyLoadingEventWith(allDeletedLocallyTasks()))

        Mockito.verify(loadingDisplayer).showLoadingScreen()
    }

    @Test
    fun `Given the current filter is all, On emission of an empty idle event, It should present to the empty screen`() {
        presenter.setInitialFilterTo(TasksActionListener.Filter.ALL)
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(emptyIdleEvent())

        Mockito.verify(loadingDisplayer).showEmptyTasksScreen()
    }

    @Test
    fun `Given the presenter is presenting, On emission of an idle event with some deleted data, It should present data`() {
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(defaultIdleEventWith(someDeletedLocallyTasks()))

        Mockito.verify(loadingDisplayer).showData()
    }

    @Test
    fun `Given the presenter is presenting, On emission of an idle event with all deleted data, It should present the empty tasks screen`() {
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(nonEmptyIdleEventWith(allDeletedLocallyTasks()))

        Mockito.verify(loadingDisplayer).showEmptyTasksScreen()
    }

    @Test
    fun `Given the current filter is active, On emission of an empty idle event, It should present the empty active tasks screen`() {
        presenter.setInitialFilterTo(TasksActionListener.Filter.ACTIVE)
        givenThePresenterIsPresenting()

        tasksActiveEventSubject.onNext(emptyIdleEvent())

        Mockito.verify(loadingDisplayer).showEmptyActiveTasksScreen()
    }

    @Test
    fun `Given the current filter is completed, On emission of an empty idle event, It should present the empty completed tasks screen`() {
        presenter.setInitialFilterTo(TasksActionListener.Filter.COMPLETED)
        givenThePresenterIsPresenting()

        tasksCompletedEventSubject.onNext(emptyIdleEvent())

        Mockito.verify(loadingDisplayer).showEmptyCompletedTasksScreen()
    }

    @Test
    fun `Given the presenter is presenting, On emission of an idle event with data, It should present data`() {
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(defaultIdleEventWith(simpleTasks()))

        Mockito.verify(loadingDisplayer).showData()
    }

    @Test
    fun `Given the presenter is presenting, On emission of an empty event, It should present the error screen`() {
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(errorEvent())

        Mockito.verify(loadingDisplayer).showErrorScreen()
    }

    @Test
    fun `Given the presenter is presenting, On emission of an error event with some deleted data, It should present the error indicator`() {
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(errorEventWith(someDeletedLocallyTasks()))

        Mockito.verify(loadingDisplayer).showErrorIndicator()
    }

    @Test
    fun `Given the presenter is presenting, On emission of an error event with all deleted data, It should present the error screen`() {
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(errorEventWith(allDeletedLocallyTasks()))

        Mockito.verify(loadingDisplayer).showErrorScreen()
    }

    @Test
    fun `Given the presenter is presenting, On emission of an error event with data, It should present the error indicator`() {
        givenThePresenterIsPresenting()

        tasksEventSubject.onNext(errorEventWith(simpleTasks()))

        Mockito.verify(loadingDisplayer).showErrorIndicator()
    }

    @Test
    fun `Given the presenter is presenting, On retry clicked, It should refresh the service`() {
        givenThePresenterIsPresenting()

        presenter.retryActionListener.onRetry()

        Mockito.verify(service).refreshTasks()
    }

    @Test
    fun `Given the presenter stopped presenting, On emission of new tasks, It should not present anything to the displayer`() {
        givenThePresenterStoppedPresenting()

        tasksEventSubject.onNext(nonEmptyIdleEventWith(simpleTasks()))

        Mockito.verifyZeroInteractions(displayer)
    }

    @Test
    fun `Given the presenter stopped presenting, On emission of an event, It should not present anything to the loading displayer`() {
        givenThePresenterStoppedPresenting()

        tasksEventSubject.onNext(defaultLoadingEvent())

        Mockito.verifyZeroInteractions(loadingDisplayer)
    }

    @Test
    fun `Given the presenter is not presenting, On startPresenting, It should attach the action listener to the loading displayer`() {
        givenThePresenterIsNotPresenting()

        presenter.startPresenting()

        Mockito.verify(loadingDisplayer).attach(presenter.retryActionListener)
    }

    @Test
    fun `Given the presenter is not presenting, On startPresenting, It should attach the action listener to the displayer`() {
        givenThePresenterIsNotPresenting()

        presenter.startPresenting()

        Mockito.verify(displayer).attach(presenter.tasksActionListener)
    }

    @Test
    fun `Given the presenter is presenting, On stopPresenting, It should detach the action listener from the loading displayer`() {
        givenThePresenterIsPresenting()

        presenter.stopPresenting()

        Mockito.verify(loadingDisplayer).detach(presenter.retryActionListener)
    }

    @Test
    fun `Given the presenter is presenting, On stopPresenting, It should detach the action listener from the displayer`() {
        givenThePresenterIsPresenting()

        presenter.stopPresenting()

        Mockito.verify(displayer).detach(presenter.tasksActionListener)
    }

    @Test
    fun `Given the presenter is not presenting, On startPresenting, It should subscribe to the tasks stream`() {
        givenThePresenterIsNotPresenting()

        presenter.startPresenting()

        assertTrue(tasksEventSubject.hasObservers())
    }

    @Test
    fun `Given the presenter is presenting, On stopPresenting, It should unsubscribe from the tasks stream`() {
        givenThePresenterIsPresenting()

        presenter.stopPresenting()

        assertFalse(tasksEventSubject.hasObservers())
    }

    @Test
    fun `Given the presenter is presenting, On toggleCompletion for a completed task, It should reactivate the task`() {
        givenThePresenterIsPresenting()
        val completedTask = simpleTask().complete()

        presenter.tasksActionListener.toggleCompletion(completedTask)

        Mockito.verify(service).activate(completedTask)
    }

    @Test
    fun `Given the presenter is presenting, On toggleCompletion for an active task, It should complete the task`() {
        givenThePresenterIsPresenting()
        val activatedTask = simpleTask().activate()

        presenter.tasksActionListener.toggleCompletion(activatedTask)

        Mockito.verify(service).complete(activatedTask)
    }

    @Test
    fun `Given the presenter is presenting, On task selected, It should navigate to task details`() {
        givenThePresenterIsPresenting()
        val simpleTask = simpleTask()

        presenter.tasksActionListener.onTaskSelected(simpleTask)

        Mockito.verify(navigator).toTaskDetail(simpleTask)
    }

    @Test
    fun `Given the presenter is presenting, On refresh selected, It should refresh the service`() {
        givenThePresenterIsPresenting()

        presenter.tasksActionListener.onRefreshSelected()

        Mockito.verify(service).refreshTasks()
    }

    @Test
    fun `Given the presenter is presenting, On clear completed selected, It should clear completed on the service`() {
        givenThePresenterIsPresenting()

        presenter.tasksActionListener.onClearCompletedSelected()

        Mockito.verify(service).clearCompletedTasks()
    }

    @Test
    fun `Given the presenter is presenting, On add task selected, It should navigate to add task`() {
        givenThePresenterIsPresenting()

        presenter.tasksActionListener.onAddTaskSelected()

        Mockito.verify(navigator).toAddTask()
    }

    @Test
    fun `Given the presenter is presenting, On all filter selected, It should subscribe to event stream`() {
        givenThePresenterIsPresenting()

        presenter.tasksActionListener.onFilterSelected(TasksActionListener.Filter.ALL)

        Mockito.verify(service, times(4)).tasksEvent
        assertTrue(tasksEventSubject.hasObservers())
    }

    @Test
    fun `Given the presenter is presenting, On active filter selected, It should subscribe to active event stream`() {
        givenThePresenterIsPresenting()

        presenter.tasksActionListener.onFilterSelected(TasksActionListener.Filter.ACTIVE)

        Mockito.verify(service, times(2)).activeTasksEvent
        assertTrue(tasksActiveEventSubject.hasObservers())
    }

    @Test
    fun `Given the presenter is presenting, On completed filter selected, It should subscribe to completed event stream`() {
        givenThePresenterIsPresenting()

        presenter.tasksActionListener.onFilterSelected(TasksActionListener.Filter.COMPLETED)

        Mockito.verify(service, times(2)).completedTasksEvent
        assertTrue(tasksCompletedEventSubject.hasObservers())
    }

    @Test
    fun `Given the presenter is presenting, On completed filter selected, It should update current filter`() {
        givenThePresenterIsPresenting()

        presenter.tasksActionListener.onFilterSelected(TasksActionListener.Filter.COMPLETED)

        assertEquals(TasksActionListener.Filter.COMPLETED, presenter.currentFilter, "Expected current filter to be updated")
    }

    @Test
    fun `Given the presenter is presenting, On active filter selected, It should update current filter`() {
        givenThePresenterIsPresenting()

        presenter.tasksActionListener.onFilterSelected(TasksActionListener.Filter.ACTIVE)

        assertEquals(TasksActionListener.Filter.ACTIVE, presenter.currentFilter, "Expected current filter to be updated")
    }

    @Test
    fun `Given the presenter is set with initial filter, On startPresenting, It should subscribe to the corresponding event stream`() {
        givenThePresenterIsNotPresenting()
        presenter.setInitialFilterTo(TasksActionListener.Filter.ACTIVE)

        presenter.startPresenting()

        Mockito.verify(service, times(2)).activeTasksEvent
        assertTrue(tasksActiveEventSubject.hasObservers())
    }

    @Test
    fun `Given the presenter is not presenting, On startPresenting, It should attach the action listener to the topLevelMenuDisplayer`() {
        givenThePresenterIsNotPresenting()

        presenter.startPresenting()

        Mockito.verify(topLevelMenuDisplayer).attach(presenter.topLevelMenuActionListener)
    }

    @Test
    fun `Given the presenter is presenting, On stopPresenting, It should detach the action listener from the topLevelMenuDisplayer`() {
        givenThePresenterIsNotPresenting()

        presenter.stopPresenting()

        Mockito.verify(topLevelMenuDisplayer).detach()
    }

    @Test
    fun `Given the presenter is presenting, On statistics item selected, It should close the top level menu and navigate to statistics`() {
        givenThePresenterIsPresenting()

        presenter.topLevelMenuActionListener.onStatisticsItemSelected()

        Mockito.verify(topLevelMenuDisplayer).closeMenu()
        Mockito.verify(navigator).toStatistics()
    }

    @Test
    fun `Given the presenter is presenting, On todo list item selected, It should close the top level menu`() {
        givenThePresenterIsPresenting()

        presenter.topLevelMenuActionListener.onToDoListItemSelected()

        Mockito.verify(topLevelMenuDisplayer).closeMenu()
        Mockito.verifyZeroInteractions(navigator)
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
    }
}
