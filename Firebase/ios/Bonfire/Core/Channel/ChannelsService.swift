import Foundation
import RxSwift

protocol ChannelsService {
    func channels(forUser user: User) -> Observable<Channels>

    func createPublicChannel(withName name: String) -> Observable<DatabaseResult<Channel>>
    func createPrivateChannel(withName name: String, owner: User) -> Observable<DatabaseResult<Channel>>

    func addOwner(owner: User, toPrivateChannel channel: Channel) -> Observable<DatabaseResult<User>>
    func removeOwner(owner: User, fromPrivateChannel channel: Channel) -> Observable<DatabaseResult<User>>

    func users(forChannel channel: Channel) -> Observable<Users>
}
