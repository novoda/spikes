import Foundation
import Firebase
import FirebaseAnalytics
import FirebaseInstanceID
import FirebaseDatabase
import RxSwift

class FirebaseChatService: ChatService {

    static let sharedInstance = FirebaseChatService()

    let firebase = FIRDatabase.database().reference()

    var messages: FIRDatabaseReference {
        return firebase.child("channels/global")
    }

    func chat() -> Observable<Chat> {
        return Observable.create { observer in
            let handle = self.messages.observeEventType(.Value, withBlock: { snapshot in
                let firebaseMessages = snapshot.children.allObjects
                let messages = try! firebaseMessages.map{$0.value}.map(Message.init)
                observer.onNext(Chat(messages: messages))
            })

            return AnonymousDisposable() {
                self.firebase.removeObserverWithHandle(handle)
            }
        }
    }

    func sendMessage(message: Message) {
        messages.push(message)
    }
}

extension FIRDatabaseReference {
    func push(value: FirebaseConvertible) {
        self.childByAutoId().setValue(value.asFirebaseValue())
    }
}
