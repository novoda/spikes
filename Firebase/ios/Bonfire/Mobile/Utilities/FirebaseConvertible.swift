import Foundation
import Firebase

protocol FirebaseConvertible {
    init(snapshot: FIRDataSnapshot) throws
    init(firebaseValue: AnyObject) throws
    func asFirebaseValue() -> AnyObject
}

extension FirebaseConvertible {
    init(snapshot: FIRDataSnapshot) throws {
        guard
            let value = snapshot.value
            else { throw FirebaseConvertibleError() }

        try self.init(firebaseValue: value)
    }
}

struct FirebaseConvertibleError: ErrorType {}
