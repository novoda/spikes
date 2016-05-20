import Foundation
import RxSwift

final class ChatPresenter {
    let loginService: LoginService
    let chatService: ChatService
    let chatDisplayer: ChatDisplayer
    let navigator: Navigator

    var disposeBag: DisposeBag!

    private var user: User?
    private let channel: Channel

    init(channel: Channel, loginService: LoginService, chatService: ChatService, chatDisplayer: ChatDisplayer, navigator: Navigator) {
        self.channel = channel
        self.loginService = loginService
        self.chatService = chatService
        self.chatDisplayer = chatDisplayer
        self.navigator = navigator
    }

    func startPresenting() {
        disposeBag = DisposeBag()

        chatDisplayer.actionListener = self

        loginService.user().subscribe(
            onNext: { [weak self] authentication in
                self?.user = authentication.user
        }).addDisposableTo(disposeBag)

        chatService.chat(channel).subscribe(
            onNext: { [weak self] chat in
                self?.chatDisplayer.display(chat)
        }).addDisposableTo(disposeBag)
    }

    func stopPresenting() {
        chatDisplayer.actionListener = nil
        disposeBag = nil
    }
}

extension ChatPresenter: ChatActionListener {
    func submitMessage(message: String) {
        guard let user = user else {
            assertionFailure("Shouldn't let user submit without a username")
            return
        }

        let msg = Message(author: user, body: message)
        chatService.sendMessage(msg, channel: channel)
    }

    func addUsers() {
        navigator.toAddUsers(channel)
    }
}
