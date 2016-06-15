import Foundation
import RxSwift
import Firebase

class FirebaseUserDatabase: UserDatabase {
    let usersDB: FIRDatabaseReference

    init(usersDB: FIRDatabaseReference) {
        self.usersDB = usersDB
    }

    func observeUsers() -> Observable<Users> {
        return usersDB.rx_readValue().map(toUsers)
    }

    func observeUser(userID: String) -> Observable<User> {
        return usersDB.child(userID).rx_readValue().map(User.init)
    }

    func readUserFrom(userID: String) -> Observable<User> {
        return usersDB.child(userID).rx_readOnce().map(User.init)
    }

    func writeCurrentUser(user: User) {
        usersDB.child(user.identifier).setValue(user.asFirebaseValue())
    }

    private func toUsers(snapshot: FIRDataSnapshot) throws -> Users {
        let users = try snapshot.children.allObjects.map(asFIRDataSnapshot).map(User.init)
        return Users(users: users)
    }
}
