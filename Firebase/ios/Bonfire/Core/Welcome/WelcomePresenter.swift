import Foundation
import RxSwift

class WelcomePresenter {
    let userService: UsersService
    let welcomeDisplayer: WelcomeDisplayer
    let navigator: Navigator
    let analytics: Analytics

    let senderID: String
    var sender: String?

    var disposeBag: DisposeBag!

    init(senderID: String,
         userService: UsersService,
         welcomeDisplayer: WelcomeDisplayer,
         navigator: Navigator,
         analytics: Analytics) {

        self.userService = userService
        self.welcomeDisplayer = welcomeDisplayer
        self.navigator = navigator
        self.analytics = analytics
        self.senderID = senderID
    }

    func startPresenting() {
        disposeBag = DisposeBag()

        userService.user(senderID)
            .subscribe(onNext: { user in
                self.welcomeDisplayer.display(user)
            }).addDisposableTo(disposeBag)
    }

    func stopPresenting() {
        disposeBag = nil
    }
}

extension WelcomePresenter: WelcomeActionListener {
    func welcomeDone() {
        navigator.dismissWelcome()
    }
}
