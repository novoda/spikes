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
    
    private var appPresenter : KGOLAppPresenter?
    private let controllButton = UIButton(frame: CGRect(x: 100, y: 100, width: 200, height: 50))
    private let boardView = UIBoard(frame: CGRect(x: 0, y: 150, width: 300, height: 300))
    
    var onControlButtonClicked: () -> KGOLStdlibUnit = {
        return KGOLStdlibUnit()
    }
    var onPatternSelected: (KGOLPatternEntity) -> KGOLStdlibUnit = {_ in
        return  KGOLStdlibUnit()
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    func renderControlButtonLabel(controlButtonLabel: String) {
        controllButton.setTitle(controlButtonLabel, for: .normal)
    }
    
    func renderPatternSelectionVisibility(visibility: Bool) {
        
    }
    
    func renderBoardWith(boardViewInput: KGOLBoardViewInput) {
        boardView.renderBoard(boardViewInput: boardViewInput)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        if (appPresenter == nil){
            let model = KGOLAppModel()
            appPresenter = KGOLAppPresenter(model: model)
            controllButton.backgroundColor = .green
            controllButton.addTarget(self, action: #selector(buttonAction), for: .touchUpInside)
            self.view.addSubview(controllButton)
            
            boardView.backgroundColor = .blue
            self.view.addSubview(boardView)
        }
        
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
