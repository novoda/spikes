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

    private let controlButton = UIButton(type: .system)
    private let patternSelectionButton = UIButton(type: .system)
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
        let cellMatrix = KGOLListBasedMatrix(width: 20, height: 20, seeds: NSArray() as! [Any])
        let boardEntity = KGOLSimulationBoardEntity(cellMatrix: cellMatrix)
        let loop = SwiftGameLoop() as KGOLGameLoop
        let model = KGOLBoardModelImpl(initialBoard: boardEntity, gameLoop: loop)
        boardPresenter = KGOLBoardPresenter(boardModel: model)

        super.init(coder: aDecoder)
        controlButton.addTarget(self, action: #selector(controlButtonAction), for: .touchUpInside)
        patternSelectionButton.addTarget(self, action: #selector(patternSelectionButtonAction), for: .touchUpInside)
    }

    private func setupLayout() {
        let yOffset: CGFloat = CGFloat(35)

        controlButton.frame = CGRect(
                x: 0,
                y: yOffset,
                width: self.view.bounds.width / 2,
                height: yOffset)

        patternSelectionButton.frame = CGRect(
                x: self.view.bounds.width / 2,
                y: yOffset,
                width: self.view.bounds.width / 2,
                height: yOffset)
        patternSelectionButton.setTitle("Choose a Pattern", for: .normal)

        boardView.backgroundColor = .black
        boardView.frame = CGRect(
                x: 0,
                y: controlButton.bounds.height + yOffset,
                width: self.view.bounds.width,
                height: self.view.bounds.width)
    }

    func renderControlButtonLabel(controlButtonLabel: String) {
        controlButton.setTitle(controlButtonLabel, for: .normal)
    }

    func renderPatternSelectionVisibility(visibility: Bool) {
        patternSelectionButton.isHidden = !visibility
    }

    func renderBoardWith(boardViewInput: KGOLBoardViewInput) {
        boardView.renderBoard(boardViewInput: boardViewInput)
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        view.addSubview(controlButton)
        view.addSubview(patternSelectionButton)
        view.addSubview(boardView)
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        setupLayout()
        appPresenter.bind(view: self)
        boardPresenter.bind(boardView: boardView)
    }

    @objc func controlButtonAction(sender: UIButton!) {
        let _ = onControlButtonClicked()
    }

    @objc func patternSelectionButtonAction(sender: UIButton!) {
        let alert = UIAlertController(title: "Patterns", message: "Please Choose a Pattern", preferredStyle: .actionSheet)

        let patterns = KGOLPatternRepositoryCompanion().patterns() as NSArray
        for pattern in patterns {
            let patternEntity: KGOLPatternEntity = pattern as! KGOLPatternEntity

            alert.addAction(UIAlertAction(title: patternEntity.getName(), style: .default, handler: { (action) in
                let _ = self.onPatternSelected(patternEntity)
            }))
        }

        self.present(alert, animated: true, completion: nil)
    }

    override func viewWillDisappear(_ animated: Bool) {
        appPresenter.unbind(view: self)
        boardPresenter.unbind(boardView: boardView)
    }

}
