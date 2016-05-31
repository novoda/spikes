import Foundation
import RxSwift

class PersistedChatService: ChatService {
    func chat(channel: Channel) -> Observable<Chat> {
        return Observable.empty()
    }

    func sendMessage(message: Message, channel: Channel) {

    }
}
