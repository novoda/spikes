package com.novoda.gol.presentation

import com.novoda.gol.GameLoop
import com.novoda.gol.data.CellMatrix
import com.novoda.gol.data.ListBasedMatrix
import com.novoda.gol.data.PositionEntity
import com.novoda.gol.data.SimulationBoardEntity
import com.novoda.gol.patterns.AbstractPattern
import com.novoda.gol.patterns.PatternEntity
import com.novoda.gol.presentation.board.BoardModelImpl
import com.novoda.gol.presentation.board.BoardPresenter
import com.novoda.gol.presentation.board.BoardView
import io.mockk.*
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BoardPresenterTest {

    private val view = mockk<BoardView>(relaxed = true)

    private val board = boardWith()

    private val gameLoop = TestGameLoop()

    private val model = BoardModelImpl(board, gameLoop)
    private val presenter = BoardPresenter(model)

    @Test
    fun initialState() {
        presenter.bind(view)

        verify {
            view.renderBoard(board)
        }
    }

    @Test
    fun clickingCellShouldRenderBoardWithCellToggled() {
        captureOnCellClickedCallback().invoke(PositionEntity(1, 1))

        verifyOrder {
            view.renderBoard(boardWith())
            view.renderBoard(boardWith(PositionEntity(1, 1)))
        }
    }

    @Test
    fun clickingSelectPatternShouldRenderBoardWithPattern() {
        captureOnPatternSelectedCallback().invoke(TestPattern.create())

        verifyOrder {
            view.renderBoard(boardWith())
            view.renderBoard(BOARD_WITH_TEST_PATTERN)
        }
    }

    @Test
    fun givenStartedClickCellsShouldBeIgnored() {
        captureOnStartSimulationCallback().invoke()
        captureOnCellClickedCallback().invoke(PositionEntity(3, 3))

        verify(exactly = 0) {
            view.renderBoard(boardWith(PositionEntity(3, 3)))
        }
    }

    @Test
    fun givenStartedPatternSelectionShouldBeIgnored() {
        captureOnStartSimulationCallback().invoke()
        captureOnPatternSelectedCallback().invoke(TestPattern.create())

        verify(exactly = 0) {
            view.renderBoard(BOARD_WITH_TEST_PATTERN)
        }
    }

    @Test
    fun shouldStartSimulation() {
        captureOnPatternSelectedCallback().invoke(TestPattern.create())
        captureOnStartSimulationCallback().invoke()

        gameLoop.tick()

        verifyOrder {
            view.renderBoard(boardWith())
            view.renderBoard(BOARD_WITH_TEST_PATTERN)
            view.renderBoard(BOARD_WITH_TEST_PATTERN_ITERATION_2)
        }
    }

    @Test
    fun shouldStartGameLoop() {
        captureOnStartSimulationCallback().invoke()

        assertTrue { gameLoop.isLooping() }
    }

    @Test
    fun shouldStopGameLoop() {
        captureOnStartSimulationCallback().invoke()
        captureOnStopSimulationCallback().invoke()

        assertFalse { gameLoop.isLooping() }
    }

    private fun captureOnCellClickedCallback(): (PositionEntity) -> Unit {
        val slot = slot<(PositionEntity) -> Unit>()
        every {
            view.onCellClicked = capture(slot)
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

    private fun captureOnStartSimulationCallback(): () -> Unit {
        val slot = slot<() -> Unit>()
        every {
            view.onStartSimulationClicked = capture(slot)
        } returns Unit

        presenter.bind(view)
        return slot.captured
    }

    private fun captureOnStopSimulationCallback(): () -> Unit {
        val slot = slot<() -> Unit>()
        every {
            view.onStopSimulationClicked = capture(slot)
        } returns Unit

        presenter.bind(view)
        return slot.captured
    }

    class TestGameLoop : GameLoop {
        override var onTick: () -> Unit = {}

        private var isLooping = false

        override fun startWith(intervalMs: Int) {
            isLooping = true
        }

        override fun stop() {
            isLooping = false
        }

        override fun isLooping() = isLooping

        fun tick() = onTick()
    }

    companion object {
        private fun boardWith(vararg seeds: PositionEntity) =
                SimulationBoardEntity(ListBasedMatrix(10, 10, seeds.toList()))

        private val BOARD_WITH_TEST_PATTERN = boardWith(
                PositionEntity(0, 1),
                PositionEntity(1, 1),
                PositionEntity(2, 1)
        )

        private val BOARD_WITH_TEST_PATTERN_ITERATION_2 = boardWith(
                PositionEntity(1, 0),
                PositionEntity(1, 1),
                PositionEntity(1, 2)
        )
    }

    class TestPattern private constructor(cellMatrix: CellMatrix) : AbstractPattern(cellMatrix) {

        override fun getName() = "TestPattern"

        companion object {
            fun create() =
                    TestPattern(ListBasedMatrix(3, 3, seeds = listOf(
                            PositionEntity(0, 1),
                            PositionEntity(1, 1),
                            PositionEntity(2, 1))))
        }
    }
}
