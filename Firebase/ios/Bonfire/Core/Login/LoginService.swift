import Foundation
import RxSwift

protocol LoginService {
    func user() -> Observable<Authentication>
    func loginWithGoogle(idToken idToken: String, accessToken: String)
    var currentUser: User? { get }
}
