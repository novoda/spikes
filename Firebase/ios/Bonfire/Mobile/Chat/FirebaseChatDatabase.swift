import Foundation
import RxSwift
import Firebase

class FirebaseChatDatabase: ChatDatabase {

    private let messagesDB: FIRDatabaseReference

    init(messagesDB: FIRDatabaseReference) {
        self.messagesDB = messagesDB
    }

    func chat(channel: Channel) -> Observable<Chat> {
        return messages(forChannel: channel)
            .rx_readValue()
            .map(toChat(forChannel: channel))
    }

    func sendMessage(message: Message, channel: Channel) {
        messages(forChannel: channel).childByAutoId().setValue(message.asFirebaseValue())
    }

    private func messages(forChannel channel: Channel) -> FIRDatabaseReference {
        return messagesDB.child(channel.name)
    }

    private func toChat(forChannel channel: Channel) -> (FIRDataSnapshot throws -> Chat) {
        return { snapshot in
            let firebaseMessages = snapshot.children.allObjects
            let messages = try firebaseMessages.map {$0.value}.map(Message.init)
            return Chat(channel: channel, messages: messages)
        }
    }
}
