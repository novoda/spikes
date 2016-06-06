import Foundation
import RxSwift

protocol ChannelsDatabase {
    func observePublicChannelIds() -> Observable<[String]>
    func observePrivateChannelIdsFor(user: User) -> Observable<[String]>
    func readChannelFor(channelName: String) -> Observable<Channel>
    func writeChannel(newChannel: Channel) -> Observable<Channel>
    func writeChannelToPublicChannelIndex(newChannel: Channel) -> Observable<Channel>
    func addOwnerToPrivateChannel(user: User, channel: Channel) -> Observable<Channel>
    func removeOwnerFromPrivateChannel(user: User, channel: Channel) -> Observable<Channel>
    func addChannelToUserPrivateChannelIndex(user: User, channel: Channel) -> Observable<Channel>
    func removeChannelFromUserPrivateChannelIndex(user: User, channel: Channel) -> Observable<Channel>
    func observeOwnerIdsFor(channel: Channel) -> Observable<[String]>
}
