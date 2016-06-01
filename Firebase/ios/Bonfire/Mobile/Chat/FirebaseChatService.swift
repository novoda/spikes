import Foundation
import Firebase
import RxSwift

final class FirebaseChatService: ChatService {

    let firebase = FIRDatabase.database().reference()

    func messages(channel: Channel) -> FIRDatabaseReference {
        return firebase.child("messages/\(channel.name)")
    }

    func chat(channel: Channel) -> Observable<DatabaseResult<Chat>> {
        return Observable.create { observer in
            let handle = self.messages(channel).observeEventType(.Value, withBlock: { snapshot in
                let firebaseMessages = snapshot.children.allObjects
                let messages = try! firebaseMessages.map{$0.value}.map(Message.init)
                observer.onNext(Chat(channel: channel, messages: messages))
            })

            return AnonymousDisposable() {
                self.firebase.removeObserverWithHandle(handle)
            }
            }.map({.Success($0)})
    }

    func sendMessage(message: Message, channel: Channel) {
        messages(channel).childByAutoId().setValue(message.asFirebaseValue())
    }
}
