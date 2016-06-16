import Foundation
import RxSwift

protocol ChatService {
    func chat(channel: Channel) -> Observable<DatabaseResult<Chat>>
    func sendMessage(message: Message, channel: Channel)
}
