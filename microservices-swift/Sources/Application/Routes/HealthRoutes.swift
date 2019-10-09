import LoggerAPI
import Health
import Kitura
import KituraContracts

class HealthRoutes {
    func initialise(with router: Router) {
        router.get("/health") { (respondWith: (Status?, RequestError?) -> Void) -> Void in
            if health.status.state == .UP {
                respondWith(health.status, nil)
            } else {
                respondWith(nil, RequestError(.serviceUnavailable, body: health.status))
            }
        }
    }
}

