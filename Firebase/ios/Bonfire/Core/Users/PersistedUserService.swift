import RxSwift

class PersistedUserService: UsersService {

    private let userDatabase: UserDatabase

    init(userDatabase: UserDatabase) {
        self.userDatabase = userDatabase
    }

    func allUsers() -> Observable<Users> {
        return userDatabase.observeUsers()
    }

    func user(userID: String) -> Observable<User> {
        return userDatabase.observeUser(userID)
    }
}
