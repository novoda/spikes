import Foundation
import RxSwift

enum ChannelCreationState {
    case Idle
    case Creating(String)
    case Success(Channel)
    case Error(ErrorType)
}

struct NoUserError: ErrorType {}

final class CreateChannelPresenter {
    let channelsService: ChannelsService
    let loginService: LoginService
    let createChannelDisplayer: CreateChannelDisplayer
    let navigator: Navigator
    var disposeBag: DisposeBag!

    private let createChannelSubject = PublishSubject<(String, Bool)>()

    init(loginService: LoginService,
         channelsService: ChannelsService,
         createChannelDisplayer: CreateChannelDisplayer,
         navigator: Navigator) {
        self.loginService = loginService
        self.channelsService = channelsService
        self.createChannelDisplayer = createChannelDisplayer
        self.navigator = navigator
    }

    func startPresenting() {
        disposeBag = DisposeBag()
        createChannelDisplayer.actionListener = self

        createChannelSubject
            .flatMap ({ (name, privateChannel) -> Observable<DatabaseResult<Channel>> in
                if privateChannel {
                    guard let user = self.loginService.currentUser else {
                        return Observable.just(.Error(NoUserError()))
                    }
                    return self.channelsService.createPrivateChannel(withName: name, owner: user)
                } else {
                    return self.channelsService.createPublicChannel(withName: name)
                }
            }).subscribe(onNext: { result in

                switch result {
                case .Success(let channel):
                    self.navigator.toChat(channel)
                case .Error(let error):
                    self.createChannelDisplayer.displayError(error)
                }

            }).addDisposableTo(disposeBag)
    }

    func stopPresenting() {
        disposeBag = nil
        createChannelDisplayer.actionListener = nil
    }
}

extension CreateChannelPresenter: CreateChannelActionListener {
    func createChannel(withName name: String, privateChannel: Bool) {
            createChannelSubject.on(.Next((name, privateChannel)))
    }
}
