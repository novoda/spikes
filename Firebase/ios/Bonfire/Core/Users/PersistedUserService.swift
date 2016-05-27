import RxSwift

class PersistedUserService: UsersService {

    private let userDatabase: UserDatabase

    init(userDatabase: UserDatabase) {
        self.userDatabase = userDatabase
    }

    func allUsers() -> Observable<[User]> {
        return userDatabase.observeUsers()
    }

}
