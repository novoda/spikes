import Foundation
import RxSwift

class FirebaseChatDatabase: ChatDatabase {

    func chat(channel: Channel) -> Observable<Chat> {
        return Observable.empty()
    }

    func sendMessage(message: Message, channel: Channel) {

    }

}
