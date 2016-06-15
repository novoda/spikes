import Foundation
import RxSwift

protocol UserDatabase {
    func observeUsers() -> Observable<Users>
    func observeUser(userID: String) -> Observable<User>
    func readUserFrom(userID: String) -> Observable<User>
    func writeCurrentUser(user: User)
}
