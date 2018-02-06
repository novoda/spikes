package com.novoda.gol.presentation

import com.novoda.gol.patterns.PatternEntity
import com.novoda.gol.patterns.PatternRepository
import com.novoda.gol.presentation.app.AppView
import com.novoda.gol.presentation.board.BoardModelImpl
import com.novoda.gol.presentation.board.BoardPresenter
import com.novoda.gol.presentation.board.BoardViewInput
import com.novoda.gol.presentation.board.apply
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import tornadofx.*

class DesktopAppView : View(), AppView {

    private lateinit var boardView: DesktopBoardView
    private lateinit var controlButton: Button
    private lateinit var patternSelectBox: ComboBox<PatternEntity>
    private var selectedPatternProperty = SimpleObjectProperty<PatternEntity>()

    override var onControlButtonClicked: () -> Unit = {}
    override var onPatternSelected: (pattern: PatternEntity) -> Unit = {}

    private val presenter = BoardPresenter(BoardModelImpl.create(20, 20))

    override val root: Parent = borderpane {
        setPrefSize(400.0, 420.0)
        top = hbox {
            button {
                controlButton = this
                action {
                    onControlButtonClicked()
                }
            }
            val items = PatternRepository
                .patterns()
                .observable()
            selectedPatternProperty.onChange { it?.let {
                onPatternSelected(it)
            } }
            combobox<PatternEntity>(selectedPatternProperty, items) {
                patternSelectBox = this
                cellFormat { text = it.getName() }
            }
        }
        boardView = DesktopBoardView()
        presenter.bind(boardView)
        center = boardView.root
    }

    override fun renderControlButtonLabel(controlButtonLabel: String) {
        controlButton.text = controlButtonLabel
    }

    override fun renderPatternSelectionVisibility(visibility: Boolean) {
        patternSelectBox.isVisible = visibility
    }

    override fun renderBoardWith(boardViewInput: BoardViewInput) {
        boardView.apply(boardViewInput)
    }
}
