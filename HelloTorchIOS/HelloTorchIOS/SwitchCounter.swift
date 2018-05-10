import Foundation
import AVFoundation

class SwitchCounter {
    
    private let lightsLogic = LightsLogic()
    private var counter: Int = 0
    private var timer: Timer = Timer()
    private var frequency: Double = 500
    private var start = DispatchTime.now()
    private var end = DispatchTime.now()
    private var isCounting = false
    
    // Start Strobe process
    func run () {
        if (self.isCounting == true) {
            print("Turning timer off")
            self.isCounting = false
            self.timer.invalidate()
            
            self.end = DispatchTime.now()
            let nanoTime = end.uptimeNanoseconds - start.uptimeNanoseconds
            let timeInterval = Double(nanoTime) / 1_000_000_000
            print("I counted this high \(counter) in this many seconds \(timeInterval) ")
            counter = 0
        } else {
            print("Turning timer on")
            self.isCounting = true
            self.timer = Timer.scheduledTimer(timeInterval: 1/frequency, target: self, selector: #selector(incrementCounter), userInfo: nil, repeats: true)
            self.start = DispatchTime.now()
        }
    }
    
    // Increase counter by one
    @objc func incrementCounter () {
        self.lightsLogic.toggleStrobe()
        self.counter += 1
    }
}
