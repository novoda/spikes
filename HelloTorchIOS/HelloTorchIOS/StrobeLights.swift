import Foundation
import AVFoundation

class StrobeLights {
    var counter: Int = 0
    var timer: Timer
    var isStrobing: Bool
    var isLightOn: Bool
    var frequency: Double
    var start = DispatchTime.now()
    var end = DispatchTime.now()
    var active: Bool
    var device: AVCaptureDevice
    
    init (){
        self.counter = 0
        self.timer = Timer()
        self.isStrobing = false
        self.isLightOn = false
        self.frequency = 500
        self.active = false
        self.device = AVCaptureDevice.default(for: AVMediaType.video)!
        do {
            try device.lockForConfiguration()
        }catch {
            print("Torch could not be used")
        }
    }
    
    // Start Strobe process
    func toggleStrobe () {
        if (device.hasTorch == false) {
            print( "torch unavailable")
            return
        }
        
        print("Is light on: \(self.isLightOn)")
        if (self.isLightOn == true) {
            self.isLightOn = false
            self.timer.invalidate()
            print("Turning timer off")
            self.end = DispatchTime.now()
            let nanoTime = end.uptimeNanoseconds - start.uptimeNanoseconds
            let timeInterval = Double(nanoTime) / 1_000_000_000
            print("I counted this high \(counter) in this many seconds \(timeInterval) ")
            counter = 0
            incrementCounter()
        } else {
            self.isLightOn = true
            self.timer = Timer.scheduledTimer(timeInterval: 1/frequency, target: self, selector: #selector(incrementCounter), userInfo: nil, repeats: true)
            print("Turning timer on")
            self.start = DispatchTime.now()
        }
    }
    
    // Increase counter by one
    @objc func incrementCounter () {
        self.toggleTorch(on: false)
        self.counter += 1
        self.toggleTorch(on: true)
    }
    // Turns light on or off
    func toggleTorch(on: Bool ) {
        if on == true {
            do {
                try device.setTorchModeOn(level: 0.5)
            }catch {
                print("Torch could not be used")
            }
        } else {
            device.torchMode = .off
        }
    }
}
