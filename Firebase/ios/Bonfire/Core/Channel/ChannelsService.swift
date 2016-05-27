import Foundation
import RxSwift

protocol ChannelsService {
    func channels(forUser user: User) -> Observable<[Channel]>

    func createPublicChannel(withName name: String) -> Observable<DatabaseWriteResult<Channel>>
    func createPrivateChannel(withName name: String, owner: User) -> Observable<DatabaseWriteResult<Channel>>

    func addOwner(owner: User, toPrivateChannel channel: Channel) -> Observable<DatabaseWriteResult<User>>
    func removeOwner(owner: User, fromPrivateChannel channel: Channel) -> Observable<DatabaseWriteResult<User>>

    func users(forChannel channel: Channel) -> Observable<[User]>
}
