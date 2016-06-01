import Foundation
import RxSwift

protocol UsersService {
    func allUsers() -> Observable<[User]>
}
