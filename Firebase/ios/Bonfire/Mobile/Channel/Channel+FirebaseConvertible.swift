import Foundation

extension Channel: FirebaseConvertible {

    init(firebaseValue: AnyObject) throws {
        guard
            let firebaseDictionary = firebaseValue as? [String: AnyObject],
            let name = firebaseDictionary["name"] as? String,
            let accessString = firebaseDictionary["access"] as? String,
            let access = Channel.Access(rawValue: accessString)
            else { throw FirebaseConvertibleError() }

        self.init(name: name, access: access)
    }

    func asFirebaseValue() -> AnyObject {
        return [
            "name": name,
            "access": access.rawValue
        ]
    }

}
