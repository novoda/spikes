import Foundation

protocol FirebaseConvertible {
    init(firebaseValue: AnyObject) throws
    func asFirebaseValue() -> AnyObject
}

struct FirebaseConvertibleError: ErrorType {}
