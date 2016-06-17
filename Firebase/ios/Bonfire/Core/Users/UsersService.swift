import Foundation
import RxSwift

protocol UsersService {
    func allUsers() -> Observable<Users>
    func user(userID: String) -> Observable<User>
}
