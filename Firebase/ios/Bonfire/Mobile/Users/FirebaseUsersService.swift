import Foundation
import RxSwift
import Firebase
import FirebaseAnalytics
import FirebaseInstanceID
import FirebaseDatabase
import FirebaseAuth
import GoogleSignIn

final class FirebaseUsersService: NSObject, UsersService {

    let usersDB = FIRDatabase.database().referenceWithPath("users")

    func allUsers() -> Observable<[User]> {
//        return usersDB.rx_readValue()
//            .map({ snapshot in
//                let firebaseUsers = snapshot.children.allObjects
//                let users = try! firebaseUsers.map{$0.value}.map(User.init)
//                return users
//            })

        return Observable.create { observer in
            let handle = self.usersDB.observeEventType(.Value, withBlock: { snapshot in
                let firebaseUsers = snapshot.children.allObjects
                let users = try! firebaseUsers.map{$0.value}.map(User.init)
                observer.onNext(users)
            })

            return AnonymousDisposable() {
                self.usersDB.removeObserverWithHandle(handle)
            }
        }
    }
}
