import Foundation

struct Channel {
    let name: String
}

// MARK - Equatable

extension Channel: Equatable {}

func ==(lhs: Channel, rhs: Channel) -> Bool {
    return lhs.name == rhs.name
}
