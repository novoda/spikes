import Foundation
import RxSwift

protocol ChannelsService {
    func channels(forUser user: User) -> Observable<[Channel]>
    func createPublicChannel(withName name: String) -> Observable<DatabaseWriteResult<Channel>>
    func createPrivateChannel(withName name: String, owners: [User]) -> Observable<DatabaseWriteResult<Channel>>
}
