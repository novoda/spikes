import Foundation
import RxSwift
import Firebase
import FirebaseAnalytics
import FirebaseInstanceID
import FirebaseDatabase
import FirebaseAuth
import GoogleSignIn

struct NoAuthAvailable: ErrorType {}

class FirebaseLoginService: NSObject, LoginService {

    let authentication = Variable<Authentication?>(nil)

    override init() {
        super.init()
    }

    func user() -> Observable<Authentication> {
        let auth = authentication.asObservable()
            .filter{ $0 != nil }
            .map { $0! }

        return initAuthentication().concat(auth)
    }

    func initAuthentication() -> Observable<Authentication> {
        return Observable.deferred { () -> Observable<Authentication> in
            if self.authentication.value?.isSuccess() ?? false {
                return Observable.empty()
            } else {
                return self.fetchUser()
            }
        }
    }

    func fetchUser() -> Observable<Authentication> {
        return Observable.create({ observer in

            if let firebaseUser = FIRAuth.auth()?.currentUser {
                let user = User(firebaseUser: firebaseUser)
                let auth = Authentication(user: user)
                observer.on(.Next(auth))
            }
            observer.on(.Completed)
            return AnonymousDisposable {}

        }).doOnNext({ auth in

            self.authentication.value = auth

        }).ignoreElements()
    }

    func loginWithGoogle(idToken idToken: String, accessToken: String) {
        let credential = FIRGoogleAuthProvider.credentialWithIDToken(idToken, accessToken: accessToken)
        FIRAuth.auth()?.signInWithCredential(credential, completion: { user, error in
            if let user = user {
                let myUser = User(firebaseUser: user)
                self.authentication.value = Authentication(user: myUser)
            } else if let error = error {
                self.authentication.value = Authentication(failure: error)
            }
        })
    }

}

extension User {
    init(firebaseUser: FIRUser) {
        self.init(name: firebaseUser.displayName!, id: firebaseUser.uid, photoURL: firebaseUser.photoURL)
    }
}
