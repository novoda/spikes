import Foundation

struct Chat {
    let messages: [Message]
}

// MARK - Equatable

extension Chat: Equatable {}

func ==(lhs: Chat, rhs: Chat) -> Bool {
    return lhs.messages == rhs.messages
}
