import Foundation
import RxSwift

protocol ChatService {
    func chat() -> Observable<Chat>
    func sendMessage(message: Message)
}
