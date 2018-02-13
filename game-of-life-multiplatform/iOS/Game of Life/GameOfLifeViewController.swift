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
    private let boardPresenter = KGOLBoardPresenter(width: 20, height: 20)
    
    var onControlButtonClicked: () -> KGOLStdlibUnit = {
        return KGOLStdlibUnit()
    }
    var onPatternSelected: (KGOLPatternEntity) -> KGOLStdlibUnit = { _ in
        return KGOLStdlibUnit()
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
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
        setupViews()
    }
    
    private func setupViews() {
        view.addSubview(controlButton)
        view.addSubview(patternSelectionButton)
        view.addSubview(boardView)
        controlButton.addTarget(self, action: #selector(controlButtonAction), for: .touchUpInside)
        patternSelectionButton.addTarget(self, action: #selector(patternSelectionButtonAction), for: .touchUpInside)
        patternSelectionButton.setTitle("Choose a Pattern", for: .normal)
        boardView.backgroundColor = .black
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
            guard let patternEntity: KGOLPatternEntity = pattern as? KGOLPatternEntity else {
                return
            }
            alert.addAction(UIAlertAction(title: patternEntity.getName(), style: .default, handler: { [weak self] action in
                let _ = self?.onPatternSelected(patternEntity)
            }))
        }
        
        self.present(alert, animated: true, completion: nil)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        appPresenter.unbind(view: self)
        boardPresenter.unbind(boardView: boardView)
    }
    
}
