//
//  UIBoardView.swift
//  Game of Life
//
//  Created by Tobias Heine on 07.02.18.
//  Copyright © 2018 Novoda. All rights reserved.
//

import UIKit
import KotlinGameOfLife

class UIBoard: UIView, KGOLBoardView {
   
    var onPatternSelected: (KGOLPatternEntity) -> KGOLStdlibUnit = {_ in
        return  KGOLStdlibUnit()
    }
    
    var onCellClicked: (KGOLPositionEntity) -> KGOLStdlibUnit = {_ in
        return KGOLStdlibUnit()
    }
    
    var onStartSimulationClicked: () -> KGOLStdlibUnit = {
        return KGOLStdlibUnit()
    }
    
    var onStopSimulationClicked: () -> KGOLStdlibUnit = {
        return KGOLStdlibUnit()
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    override init(frame: CGRect){
        super.init(frame: frame)
    }
    
    func renderBoard(boardViewInput:KGOLBoardViewInput) {
        if (boardViewInput.selectedPattern != nil) {
            onPatternSelected(boardViewInput.selectedPattern!)
        }
        
        if (boardViewInput.isIdle == false) {
            onStartSimulationClicked()
        } else {
            onStopSimulationClicked()
        }
    }
    
    func renderBoard(boardEntity: KGOLBoardEntity) {
        subviews.forEach { $0.removeFromSuperview() }
        
        let cellDimen = bounds.size.width / CGFloat(boardEntity.getWidth())
        
        for y in 0...boardEntity.getHeight()-1{
            for x in 0...boardEntity.getWidth()-1{
             let cell = boardEntity.cellAtPosition(x: x, y: y)
                
                let position = KGOLPositionEntity(x:x,y:y)
                let cellView = UICell(frame: CGRect(x: CGFloat(x) * cellDimen, y: CGFloat(y) * cellDimen, width: cellDimen, height: cellDimen),position:position)
                
                cellView.addTarget(self, action: #selector(onCellClicked(_:)), for: .touchUpInside)
                
                if (cell.isAlive){
                    cellView.backgroundColor = .black
                }
                
                addSubview(cellView)
            }
        }
    }
    
    @objc func onCellClicked(_ sender: UICell) {
        guard let position = sender.position else { return }
        onCellClicked(position)
    }
}
