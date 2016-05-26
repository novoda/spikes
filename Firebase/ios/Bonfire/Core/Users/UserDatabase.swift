import Foundation
import RxSwift

protocol UserDatabase {
    func observeUsers() -> Observable<[User]>
    func readUserFrom(userID: String) -> Observable<User>
    func writeCurrentUser(user: User)
}
