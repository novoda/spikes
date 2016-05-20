import Foundation

struct Channel {
    enum Access: String {
        case Public = "public"
        case Private = "private"
    }

    let name: String
    let access: Access
}

// MARK - Equatable

extension Channel: Equatable {}

func ==(lhs: Channel, rhs: Channel) -> Bool {
    return lhs.name == rhs.name &&
        lhs.access == rhs.access
}
