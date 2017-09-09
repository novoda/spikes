import Foundation

class TweetsProvider {
    
    func fetchTweets(completion: @escaping (_ tweets: [Tweet]) -> Void) {
        completion(createTweets())
    }
    
    private func createTweets() -> [Tweet] {
        let mrFox = createUser(name: "Mr Fox")
        let kristofferson = createUser(name: "Kristofferson")
        let mrsFox = createUser(name: "Mrs Fox")
        let ash = createUser(name: "Ash")
        let badger = createUser(name: "Badger")
        let mole = createUser(name: "Mole")
        
        return [
            Tweet(id: "1", user: mrFox, body: "Are you cussing with me?", time: "23:12"),
            Tweet(id: "2", user: kristofferson, body: "...I've got mixed feelings about that", time: "22:20"),
            Tweet(id: "3", user: mrsFox, body: "If what I think is happening, is happening...It'd better not be", time: "20:56"),
            Tweet(id: "4", user: ash, body: "No you're not. You're disloyal.", time: "19:11"),
            Tweet(id: "5", user: mrFox, body: "I understand what you're saying, and your comments are valuable, but I'm gonna ignore your advice.", time: "15:04"),
            Tweet(id: "6", user: badger, body: "If you're gonna cuss with somebody, you're not gonna cuss with me, you little cuss!", time: "14:44"),
            Tweet(id: "7", user: mrFox, body: "You are without a doubt the five and a half most wonderful wild animals I've ever met in my life", time: "14:32"),
            Tweet(id: "8", user: mole, body: "I'm sick of your double talk, we have rights!", time: "12:08"),
            Tweet(id: "9", user: mrFox, body: "Pensez-vous que l'hiver sera rude?", time: "12:05"),
            Tweet(id: "10", user: mrsFox, body: "You know, you really are... fantastic.", time: "10:37")
        ]
    }
    
    private func createUser(name: String) -> User {
        let generatedUrl = "https://robohash.org/\(name.addingPercentEncoding(withAllowedCharacters: .urlHostAllowed)!)"
        return User(username: name, avatarUrl: generatedUrl)
    }
}
