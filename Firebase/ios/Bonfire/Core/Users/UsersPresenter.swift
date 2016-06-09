import Foundation
import RxSwift

class UsersPresenter {

    let usersService: UsersService
    let channelsService: ChannelsService
    let usersDisplayer: UsersDisplayer
    let navigator: Navigator

    var disposeBag: DisposeBag!

    let channel: Channel

    enum UserAction {
        case Add(User)
        case Remove(User)
    }

    private let updateUserSubject = PublishSubject<UserAction>()

    init(channel: Channel,
         usersService: UsersService,
         channelsService: ChannelsService,
         usersDisplayer: UsersDisplayer,
         navigator: Navigator) {
        self.channel = channel
        self.usersService = usersService
        self.channelsService = channelsService
        self.usersDisplayer = usersDisplayer
        self.navigator = navigator
    }

    func startPresenting() {
        disposeBag = DisposeBag()

        usersDisplayer.actionListener = self

        let usersObservable = Observable.combineLatest(
            usersService.allUsers(),
            channelsService.users(forChannel: channel)
        ) { (allUsers, channelOwners) in
            return (allUsers, channelOwners)
        }

        usersObservable.subscribe(
            onNext: { [weak self] users, channelOwners in
                self?.usersDisplayer.display(users, channelOwners: channelOwners)
            }).addDisposableTo(disposeBag)


        updateUserSubject
            .flatMap({ userAction in
                self.applyUserAction(userAction)
            })
            .subscribe(
                onError: { error in
                    print(error)
            }).addDisposableTo(disposeBag)
    }

    func stopPresenting() {
        usersDisplayer.actionListener = nil
        disposeBag = nil
    }

    private func applyUserAction(userAction: UserAction) -> Observable<DatabaseResult<User>> {
        switch userAction {
        case .Add(let user):
            return channelsService.addOwner(user, toPrivateChannel: channel)
        case .Remove(let user):
            return channelsService.removeOwner(user, fromPrivateChannel: channel)
        }
    }
}

extension UsersPresenter: UsersActionListener {

    func addOwner(user: User) {
        updateUserSubject.onNext(.Add(user))
    }

    func removeOwner(user: User) {
        updateUserSubject.onNext(.Remove(user))
    }

}
