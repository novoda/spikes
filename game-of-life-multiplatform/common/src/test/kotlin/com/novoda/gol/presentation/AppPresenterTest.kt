package com.novoda.gol.presentation

import com.novoda.gol.patterns.Glider
import com.novoda.gol.patterns.PatternEntity
import io.mockk.*
import kotlin.test.Test

class AppPresenterTest {
    private val view = mockk<AppView>(relaxed = true)

    private val model = AppModel()
    private val presenter = AppPresenter(model)

    @Test
    fun bind() {
        presenter.bind(view)

        verify {
            view.onControlButtonClicked = any()
            view.onPatternSelected = any()
        }
    }

    @Test
    fun initialState() {
        presenter.bind(view)

        verify {
            view.renderControlButtonLabel("Start Simulation")
            view.renderPatternSelectionVisibility(true)
            view.renderBoardWith(BoardViewInput(isIdle = true))
        }
    }

    @Test
    fun clickingControlButtonShouldChangeButtonText() {
        captureOnControlButtonCallback().invoke()

        verify { view.renderControlButtonLabel("Stop Simulation") }
    }

    @Test
    fun clickingControlButtonShouldHidePatternSelection() {
        captureOnControlButtonCallback().invoke()

        verify { view.renderPatternSelectionVisibility(false) }
    }

    @Test
    fun clickingControlButtonShouldRendereNonIdleBoard() {
        captureOnControlButtonCallback().invoke()

        verify { view.renderBoardWith(BoardViewInput(isIdle = false)) }
    }

    @Test
    fun shouldRenderBoardWithSelectedPattern() {
        captureOnPatternSelectedCallback().invoke(Glider.create())

        verify { view.renderBoardWith(BoardViewInput(true, Glider.create())) }
    }

    @Test
    fun givenSimulationRunningShouldIgnorePatternSelection() {
        captureOnControlButtonCallback().invoke()
        captureOnPatternSelectedCallback().invoke(Glider.create())

        verify(exactly = 0) {
            view.renderBoardWith(BoardViewInput(false, Glider.create()))
        }
    }

    @Test
    fun givenIsStartedClickingControlButtonShouldStopSimulation() {
        captureOnControlButtonCallback().invoke()
        captureOnControlButtonCallback().invoke()

        verifyOrder {
            view.renderBoardWith(BoardViewInput(false))
            view.renderBoardWith(BoardViewInput(true))
        }
    }

    private fun captureOnControlButtonCallback(): () -> Unit {
        val slot = slot<() -> Unit>()
        every {
            view.onControlButtonClicked = capture(slot)
        } returns Unit

        presenter.bind(view)
        return slot.captured
    }

    private fun captureOnPatternSelectedCallback(): (PatternEntity) -> Unit {
        val slot = slot<(PatternEntity) -> Unit>()
        every {
            view.onPatternSelected = capture(slot)
        } returns Unit

        presenter.bind(view)
        return slot.captured
    }

}
