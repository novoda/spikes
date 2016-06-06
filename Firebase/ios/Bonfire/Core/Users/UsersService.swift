import Foundation
import RxSwift

protocol UsersService {
    func allUsers() -> Observable<Users>
}
