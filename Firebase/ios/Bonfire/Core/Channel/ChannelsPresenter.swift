import Foundation
import RxSwift

class ChannelsPresenter {
    let loginService: LoginService
    let channelsService: ChannelsService
    let channelsDisplayer: ChannelsDisplayer
    let navigator: Navigator
    let dynamicLinkFactory: DynamicLinkFactory
    let config: Config

    var disposeBag: DisposeBag!

    init(loginService: LoginService,
         channelsService: ChannelsService,
         channelsDisplayer: ChannelsDisplayer,
         navigator: Navigator,
         dynamicLinkFactory: DynamicLinkFactory,
         config: Config) {
        self.loginService = loginService
        self.channelsService = channelsService
        self.channelsDisplayer = channelsDisplayer
        self.navigator = navigator
        self.dynamicLinkFactory = dynamicLinkFactory
        self.config = config
    }

    func startPresenting() {
        disposeBag = DisposeBag()

        channelsDisplayer.attach(self)

        loginService.user().filter({ auth in
            auth.isSuccess()
        }).flatMap({ auth in
            return self.channelsService.channels(forUser: auth.user!)
        }).map({ channels in
            if self.config.orderChannelsByName() {
                return channels.sorted()
            } else {
                return channels
            }
        }).subscribe(
            onNext: { [weak self] channels in
                self?.channelsDisplayer.display(channels)
            }).addDisposableTo(disposeBag)
    }

    func stopPresenting() {
        channelsDisplayer.detach(self)
        disposeBag = nil
    }
}

extension ChannelsPresenter: ChannelsActionListener {
    func viewChannel(channel: Channel) {
        navigator.toChat(channel)
    }

    func goToNewChannel() {
        navigator.toCreateChannel()
    }

    func shareBonfire() {
        guard let user = loginService.currentUser else { return }
        let shareURL = dynamicLinkFactory.inviteLinkFromUser(user)
        let message = "Check out Bonfire!"
        let parameters = [message, shareURL]
        navigator.showShareSheet(parameters)
    }
}
