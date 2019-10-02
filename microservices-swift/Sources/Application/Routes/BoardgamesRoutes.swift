import LoggerAPI
import KituraContracts

class BoardgameRoutes {
    private static let seed = [Boardgame(id: 1, name: "Risk", emoji: "💣"), Boardgame(id: 2, name: "Uno", emoji: "1️⃣")]
    private var boardgames: [Boardgame] = BoardgameRoutes.seed
    
    public func initialise(app: App) {
        let seed = [Boardgame(id: 1, name: "Risk", emoji: "💣"), Boardgame(id: 2, name: "Uno", emoji: "1️⃣")]
        var boardgames: [Boardgame] = seed
        
        app.router.get("/boardgames") { (respondWith: ([Boardgame]?, RequestError?) -> Void) -> Void in
            respondWith(boardgames, nil)
        }

        app.router.post("/boardgames") { (boardgame: Boardgame, respondWith: (Boardgame?, RequestError?) -> Void) -> Void in
            boardgames.append(boardgame)
            
            respondWith(boardgame, nil)
        }

        app.router.delete("/boardgames") { (boardgameId: Int, respondWith: (RequestError?) -> Void) -> Void in
            guard let index = boardgames.firstIndex(where: { $0.id == boardgameId }) else {
                respondWith(RequestError.notFound)
                return
            }
            boardgames.remove(at: index)
            respondWith(nil)
        }

        // PUT?
        // UPDATE?
        // error for inserting boardgame with existing id
        // update swagger

        // Next Up!
        // database?
        // deploy?
        
    }
}
