import Foundation
import RxSwift

protocol ChatDatabase {
    func chat(channel: Channel) -> Observable<Chat>
    func sendMessage(message: Message, channel: Channel)
}
