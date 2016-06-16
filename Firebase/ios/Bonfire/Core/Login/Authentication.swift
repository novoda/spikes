import Foundation

struct Authentication {
    let user: User?
    let failure: ErrorType?

    init(user: User) {
        self.user = user
        self.failure = nil
    }

    init(failure: ErrorType) {
        self.user = nil
        self.failure = failure
    }

    func isSuccess() -> Bool {
        return user != nil
    }
}
