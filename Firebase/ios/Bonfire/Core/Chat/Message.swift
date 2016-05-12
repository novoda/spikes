import Foundation

typealias Timestamp = Int64

struct Message {
    let author: User
    let body: String
    let timestamp: Timestamp
}

extension Message {
    init(author: User, body: String) {
        self.author = author
        self.body = body
        self.timestamp = Timestamp(NSDate().timeIntervalSince1970 * 1000)
    }
}

extension Message {
    func toString() -> String {
        return "\(author.name): \(body)"
    }
}

// MARK: - Equatable

extension Message: Equatable {}

func ==(lhs: Message, rhs: Message) -> Bool {
    return lhs.author == rhs.author &&
        lhs.body == rhs.body &&
        lhs.timestamp == rhs.timestamp
}
