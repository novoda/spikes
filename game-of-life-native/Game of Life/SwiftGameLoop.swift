//
//  SwiftGameLoop.swift
//  Game of Life
//
//  Created by Tobias Heine on 07.02.18.
//  Copyright © 2018 Novoda. All rights reserved.
//

import UIKit
import KotlinGameOfLife

class SwiftGameLoop: NSObject, KGOLGameLoop {

    var displayLink : CADisplayLink?

    func startWith(intervalMs: Int32) {
        displayLink = CADisplayLink(target: self, selector: #selector(handleTimer))
        displayLink?.preferredFramesPerSecond = 3
        displayLink?.add(to: .main, forMode: .commonModes)
    }

    @objc func handleTimer() {
        onTick()
    }

    func stop() {
        displayLink?.invalidate()
        displayLink = nil
    }

    func isLooping() -> Bool {
        return displayLink != nil
    }

    var onTick: () -> KGOLStdlibUnit = {
        return KGOLStdlibUnit()
    }

}

