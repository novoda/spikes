import LoggerAPI
import Health
import KituraContracts

func initializeHealthRoutes(app: App) {
    
    app.router.get("/health") { (respondWith: (Status?, RequestError?) -> Void) -> Void in
        if health.status.state == .UP {
            respondWith(health.status, nil)
        } else {
            respondWith(nil, RequestError(.serviceUnavailable, body: health.status))
        }
    }
    
    app.router.get("/boardgames") { (respondWith: ([Boardgame]?, RequestError?) -> Void) -> Void in
        let boardgame1 = Boardgame(id: 1, name: "Risk", emoji: "💣")
        let boardgame2 = Boardgame(id: 2, name: "Uno", emoji: "1️⃣")
        
        respondWith([boardgame1, boardgame2], nil)
    }
    
}
