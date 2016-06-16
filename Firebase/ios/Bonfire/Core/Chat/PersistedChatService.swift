import Foundation
import RxSwift

final class PersistedChatService: ChatService {
    private let chatDatabase: ChatDatabase

    init(chatDatabase: ChatDatabase) {
        self.chatDatabase = chatDatabase
    }

    func chat(channel: Channel) -> Observable<DatabaseResult<Chat>> {
        return chatDatabase.chat(channel)
            .map({.Success($0)})
            .catchError({Observable.just(.Error($0))})
    }

    func sendMessage(message: Message, channel: Channel) {
        chatDatabase.sendMessage(message, channel: channel)
    }
}
