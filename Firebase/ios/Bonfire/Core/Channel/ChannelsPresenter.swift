import Foundation
import RxSwift

class ChannelsPresenter {
    let channelsService: ChannelsService
    let channelsDisplayer: ChannelsDisplayer
    let navigator: Navigator

    var disposeBag: DisposeBag!

    init(channelsService: ChannelsService, channelsDisplayer: ChannelsDisplayer, navigator: Navigator) {
        self.channelsService = channelsService
        self.channelsDisplayer = channelsDisplayer
        self.navigator = navigator
    }

    func startPresenting() {
        disposeBag = DisposeBag()

        channelsDisplayer.attach(self)

        channelsService.channels().subscribe(
            onNext: { [weak self] channels in
                self?.channelsDisplayer.display(channels)
            }
        ).addDisposableTo(disposeBag)
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