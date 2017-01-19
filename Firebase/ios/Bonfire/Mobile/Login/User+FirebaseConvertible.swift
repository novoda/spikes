import Foundation

//MARK: - FirebaseConvertible

extension User: FirebaseConvertible {

    init(firebaseValue: AnyObject) throws {
        guard
            let firebaseDictionary = firebaseValue as? [String: AnyObject],
            let name = firebaseDictionary["name"] as? String,
            let id = firebaseDictionary["id"] as? String
            else { throw FirebaseConvertibleError() }

        let photoURL: NSURL?
        if let photoURLString = firebaseDictionary["photoUrl"] as? String {
            photoURL = NSURL(string: photoURLString)
        } else {
            photoURL = nil
        }

        self.init(name: name, id: id, photoURL: photoURL)
    }

    func asFirebaseValue() -> AnyObject {
        return [
            "name": name,
            "id": id,
            "photoUrl": photoURL?.absoluteString ?? ""
        ]
    }
    
}
