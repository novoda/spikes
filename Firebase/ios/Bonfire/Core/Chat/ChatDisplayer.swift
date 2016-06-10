import Foundation

protocol ChatActionListener: class {
    func submitMessage(message: String)
    func addUsers()
}

protocol ChatDisplayer: class {
    func display(chat: Chat, forUser user: User)
    weak var actionListener: ChatActionListener? { get set }
}
