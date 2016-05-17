import Foundation

protocol ChatActionListener: class {
    func submitMessage(message: String)
}

protocol ChatDisplayer {
    func display(chat: Chat)
    func attach(actionListener: ChatActionListener)
    func detach(actionListener: ChatActionListener)
}
