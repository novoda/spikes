import Foundation

// MARK: - FirebaseConvertible

extension Message: FirebaseConvertible {
    init(firebaseValue: AnyObject) throws {
        guard
            let firebaseDictionary = firebaseValue as? [String: AnyObject],
            let authorDictionary = firebaseDictionary["author"],
            let body = firebaseDictionary["body"] as? String,
            let timestamp = firebaseDictionary["timestamp"] as? NSNumber
            else { throw FirebaseConvertibleError() }

        let author = try User(firebaseValue: authorDictionary)
        self.init(author: author, body: body, timestamp: timestamp.longLongValue)
    }

    func asFirebaseValue() -> AnyObject {
        return [
            "author": author.asFirebaseValue(),
            "body": body,
            "timestamp": NSNumber(longLong: timestamp)
        ]
    }
}
