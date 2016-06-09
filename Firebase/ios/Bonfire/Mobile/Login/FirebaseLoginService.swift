import Foundation
import RxSwift
import Firebase
import GoogleSignIn

struct NoAuthAvailable: ErrorType {}

final class FirebaseLoginService: NSObject, LoginService {

    let usersDB = FIRDatabase.database().referenceWithPath("users")
    let authentication = Variable<Authentication?>(nil)

    var currentUser: User? {
        guard let firebaseUser = FIRAuth.auth()?.currentUser else {
            return nil
        }
        return User(firebaseUser: firebaseUser)
    }

    func user() -> Observable<Authentication> {
        let auth = authentication.asObservable()
            .filter { $0 != nil }
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
        FIRAuth.auth()?.signInWithCredential(credential, completion: { firebaseUser, error in
            if let user = firebaseUser {
                let user = User(firebaseUser: user)
                self.usersDB.child(user.identifier).setValue(user.asFirebaseValue())
                self.authentication.value = Authentication(user: user)
            } else if let error = error {
                self.authentication.value = Authentication(failure: error)
            }
        })
    }

}

extension User {
    init(firebaseUser: FIRUser) {
        self.init(name: firebaseUser.displayName!, identifier: firebaseUser.uid, photoURL: firebaseUser.photoURL)
    }
}
