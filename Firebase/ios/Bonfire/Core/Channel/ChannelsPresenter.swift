import Foundation
import RxSwift

class ChannelsPresenter {
    let channelsService: ChannelsService
    let channelsDisplayer: ChannelsDisplayer

    var disposeBag: DisposeBag!

    init(channelsService: ChannelsService, channelsDisplayer: ChannelsDisplayer) {
        self.channelsService = channelsService
        self.channelsDisplayer = channelsDisplayer
    }

    func startPresenting() {
        disposeBag = DisposeBag()
        channelsService.channels().subscribe(
            onNext: { [weak self] channels in
                self?.channelsDisplayer.display(channels)
            }
        ).addDisposableTo(disposeBag)
    }

    func stopPresenting() {
        disposeBag = nil
    }
}
