import Foundation
import RxSwift

protocol ChannelsService {
    func channels(forUser user: User) -> Observable<[Channel]>
    func users(forChannel channel: Channel) -> Observable<[User]>

    func createPublicChannel(withName name: String) -> Observable<DatabaseWriteResult<Channel>>
    func createPrivateChannel(withName name: String, owners: [User]) -> Observable<DatabaseWriteResult<Channel>>

    func addOwners(owners: [User], channel: Channel) -> Observable<DatabaseWriteResult<[User]>>
    func removeOwners(owners: [User], channel: Channel) -> Observable<DatabaseWriteResult<[User]>>
}
