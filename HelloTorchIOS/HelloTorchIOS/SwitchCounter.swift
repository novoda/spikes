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
    
    func run () {
        if (isCounting) {
            stopCounting()
            stopTimer()
            printCounter()
            resetCounter()
        } else {
            print("Turning timer on")
            startCounting()
            startTimer()
        }
    }
    
    fileprivate func stopCounting() {
        isCounting = false
    }
    
    fileprivate func startCounting() {
        isCounting = true
    }
    
    fileprivate func stopTimer() {
        timer.invalidate()
        end = DispatchTime.now()
    }
    
    fileprivate func startTimer() {
        timer = Timer.scheduledTimer(timeInterval: 1/frequency, target: self, selector: #selector(incrementCounter), userInfo: nil, repeats: true)
        start = DispatchTime.now()
    }
    
    fileprivate func printCounter() {
        print("I counted this high \(counter) in this many seconds \(timeInterval()) ")
    }
    
    fileprivate func timeInterval() -> Double {
        let nanoTime = end.uptimeNanoseconds - start.uptimeNanoseconds
        return Double(nanoTime) / 1_000_000_000
    }
    
    fileprivate func resetCounter() {
        counter = 0
    }
    
    @objc func incrementCounter () {
        lightsLogic.toggleStrobe()
        counter += 1
    }
}
