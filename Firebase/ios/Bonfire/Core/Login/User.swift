import Foundation

struct User {
    let name: String
    let id: String
    let photoURL: NSURL?
}

// MARK - Equatable

extension User: Equatable {}

func ==(lhs: User, rhs: User) -> Bool {
    return lhs.name == rhs.name &&
        lhs.id == rhs.id &&
        lhs.photoURL == rhs.photoURL
}

