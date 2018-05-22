//
//  LightsLogic.swift
//  HelloTorchIOS
//
//  Created by Luis Valle on 08/05/2018.
//  Copyright © 2018 Novoda. All rights reserved.
//

import Foundation
import AVFoundation

class LightsLogic {
    
    private let device: AVCaptureDevice? = AVCaptureDevice.default(for: AVMediaType.video)
    
    init() {
        do {
            try device?.lockForConfiguration()
        }catch {
            print("Torch could not be used")
        }
    }
    
    func toggleStrobe() {
        guard device?.hasTorch == true else {
            print( "torch unavailable")
            return
        }
        
        if isLightOn() {
            turnLightOff()
        } else {
            turnLightOn()
        }
    }
    
    private func turnLightOn() {
        do {
            try device?.setTorchModeOn(level: 0.5)
        }catch {
            print("Torch could not be used")
        }
    }
    
    private func turnLightOff() {
        device?.torchMode = .off
    }
    
    private func isLightOn() -> Bool {
        return device?.torchMode == .on
    }
}
