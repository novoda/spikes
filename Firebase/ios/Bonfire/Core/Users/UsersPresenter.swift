import Foundation
import RxSwift

class UsersPresenter {

    let usersService: UsersService
    let channelsService: ChannelsService
    let usersDisplayer: UsersDisplayer
    let navigator: Navigator

    var disposeBag: DisposeBag!

    let channel: Channel

    init(channel: Channel, usersService: UsersService, channelsService: ChannelsService, usersDisplayer: UsersDisplayer, navigator: Navigator) {
        self.channel = channel
        self.usersService = usersService
        self.channelsService = channelsService
        self.usersDisplayer = usersDisplayer
        self.navigator = navigator
    }

    func startPresenting() {
        disposeBag = DisposeBag()

        usersDisplayer.actionListener = self

        usersService.allUsers().subscribe(
            onNext: { [weak self] users in
                self?.usersDisplayer.display(users)
            }
        ).addDisposableTo(disposeBag)
    }

    func stopPresenting() {
        usersDisplayer.actionListener = nil
        disposeBag = nil
    }

}

extension UsersPresenter: UsersActionListener {

    func addOwner(user: User) {
        channelsService.addOwners([user], channel: channel)
    }

    func removeOwner(user: User) {
        channelsService.removeOwners([user], channel: channel)
    }

}
