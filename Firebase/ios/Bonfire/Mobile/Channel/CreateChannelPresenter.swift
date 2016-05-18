import Foundation
import RxSwift

enum ChannelCreationState {
    case Idle
    case Creating(String)
    case Success(Channel)
    case Error(ErrorType)
}

final class CreateChannelPresenter {
    let channelService: ChannelsService
    let createChannelDisplayer: CreateChannelDisplayer
    let navigator: Navigator

    var disposeBag: DisposeBag!

    private let createChannelSubject = PublishSubject<String>()

    init(channelService: ChannelsService, createChannelDisplayer: CreateChannelDisplayer, navigator: Navigator) {
        self.channelService = channelService
        self.createChannelDisplayer = createChannelDisplayer
        self.navigator = navigator
    }

    func startPresenting() {
        disposeBag = DisposeBag()

        createChannelSubject
            .flatMap ({ name in

                self.channelService.createPublicChannel(withName: name)

            }).subscribe(onNext: { result in

                switch result {
                case .Success(let channel):
                    self.navigator.toChat(channel)
                case .Error(let error):
                    self.createChannelDisplayer.displayError(error)
                }

            }).addDisposableTo(disposeBag)
    }
}

extension CreateChannelPresenter: CreateChannelActionListener {
    func createChannel(withName name: String) {
        createChannelSubject.on(.Next(name))
    }
}


