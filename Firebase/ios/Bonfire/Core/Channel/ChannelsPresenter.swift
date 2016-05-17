import Foundation
import RxSwift

class ChannelsPresenter {
    let loginService: LoginService
    let channelsService: ChannelsService
    let channelsDisplayer: ChannelsDisplayer
    let navigator: Navigator

    var disposeBag: DisposeBag!

    init(loginService: LoginService, channelsService: ChannelsService, channelsDisplayer: ChannelsDisplayer, navigator: Navigator) {
        self.loginService = loginService
        self.channelsService = channelsService
        self.channelsDisplayer = channelsDisplayer
        self.navigator = navigator
    }

    func startPresenting() {
        disposeBag = DisposeBag()

        channelsDisplayer.attach(self)

        loginService.user().filter({ auth in
            auth.isSuccess()
        }).flatMap({ auth in
            return self.channelsService.channels(forUser: auth.user!)
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
}