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

    private let controlButton = UIButton()
    private let boardView = UIBoard()
    private let appPresenter = KGOLAppPresenter(model: KGOLAppModel())
    private let boardPresenter: KGOLBoardPresenter

    var onControlButtonClicked: () -> KGOLStdlibUnit = {
        return KGOLStdlibUnit()
    }
    var onPatternSelected: (KGOLPatternEntity) -> KGOLStdlibUnit = { _ in
        return KGOLStdlibUnit()
    }

    required init?(coder aDecoder: NSCoder) {
        controlButton.backgroundColor = .green
        boardView.backgroundColor = .blue

        let cellMatrix = KGOLListBasedMatrix(width: 20, height: 20, seeds: NSArray() as! [Any])
        let boardEntity = KGOLSimulationBoardEntity(cellMatrix: cellMatrix)
        let loop = SwiftGameLoop() as KGOLGameLoop
        let model = KGOLBoardModelImpl(initialBoard: boardEntity, gameLoop: loop)
        boardPresenter = KGOLBoardPresenter(boardModel: model)

        super.init(coder: aDecoder)
        controlButton.addTarget(self, action: #selector(buttonAction), for: .touchUpInside)
    }

    private func setupLayout() {
        controlButton.pinToSuperview(edges: [.left, .top], constant: 35)
        controlButton.addWidthConstraint(with: 75)

        boardView.pinToSuperview(edges: [.right, .left])
        boardView.pin(edge: .top, to: .bottom, of: controlButton)
        boardView.heightAnchor.constraint(equalTo: boardView.widthAnchor).isActive = true
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
        setupLayout()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        appPresenter.bind(view: self)
        boardPresenter.bind(boardView: boardView)
    }

    @objc func buttonAction(sender: UIButton!) {
        let _ = onControlButtonClicked()
    }

    override func viewWillDisappear(_ animated: Bool) {
        appPresenter.unbind(view: self)
        boardPresenter.unbind(boardView: boardView)
    }

}
