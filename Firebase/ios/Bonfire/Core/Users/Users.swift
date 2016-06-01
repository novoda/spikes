import Foundation

struct Users {
    let users: [User]

    var count: Int {
        return users.count
    }

    func contains(user: User) -> Bool {
        return users.contains(user)
    }
}

extension Users {
    init() {
        self.users = []
    }
}

// MARK - Equatable

extension Users: Equatable {}

func ==(lhs: Users, rhs: Users) -> Bool {
    return lhs.users == rhs.users
}
