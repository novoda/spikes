//
//  ViewController.swift
//  Game of Life
//
//  Created by Tobias Heine on 06.02.18.
//  Copyright © 2018 Novoda. All rights reserved.
//

import UIKit
import Foundation
import KotlinGameOfLife

class GameOfLifeViewController: UIViewController, KGOLAppView {

    private var appPresenter: KGOLAppPresenter?
    private let controlButton: UIButton
    private let boardView: UIBoard

    var onControlButtonClicked: () -> KGOLStdlibUnit = {
        return KGOLStdlibUnit()
    }
    var onPatternSelected: (KGOLPatternEntity) -> KGOLStdlibUnit = { _ in
        return KGOLStdlibUnit()
    }

    required init?(coder aDecoder: NSCoder) {
        controlButton = UIButton(frame: CGRect(x: 100, y: 100, width: 200, height: 50))
        controlButton.backgroundColor = .green
        boardView = UIBoard(frame: CGRect(x: 0, y: 150, width: 300, height: 300))
        boardView.backgroundColor = .blue
        super.init(coder: aDecoder)

        controlButton.addTarget(self, action: #selector(buttonAction), for: .touchUpInside)
    }

    func renderControlButtonLabel(controlButtonLabel: String) {
        controlButton.setTitle(controlButtonLabel, for: .normal)
    }

    func renderPatternSelectionVisibility(visibility: Bool) {

    }

    func renderBoardWith(boardViewInput: KGOLBoardViewInput) {
        boardView.renderBoard(boardViewInput: boardViewInput)
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        view.addSubview(controlButton)
        view.addSubview(boardView)
        
        appPresenter = KGOLAppPresenter(model: KGOLAppModel())
        appPresenter?.bind(view: self)
        boardView.willAppear()
    }

    @objc func buttonAction(sender: UIButton!) {
        onControlButtonClicked()
    }

    override func viewWillDisappear(_ animated: Bool) {
        appPresenter?.unbind(view: self)
        boardView.willDisAppear()
    }

}
