import Foundation

struct User {
    let name: String
    let identifier: String
    let photoURL: NSURL?
}

// MARK - Equatable

extension User: Equatable {}

func ==(lhs: User, rhs: User) -> Bool {
    return lhs.name == rhs.name &&
        lhs.identifier == rhs.identifier &&
        lhs.photoURL == rhs.photoURL
}
