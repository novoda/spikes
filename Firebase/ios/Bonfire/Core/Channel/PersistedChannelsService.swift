import Foundation
import RxSwift

class PersistedChannelsService: ChannelsService {
    let channelsDatabase: ChannelsDatabase
    let userDatabase: UserDatabase

    // MARK: - Read

    init(channelsDatabase: ChannelsDatabase, userDatabase: UserDatabase) {
        self.channelsDatabase = channelsDatabase
        self.userDatabase = userDatabase
    }

    func channels(forUser user: User) -> Observable<Channels> {
        return Observable.combineLatest(
            publicChannels(),
            privateChannels(forUser: user)
        ) { publicChannels, privateChannels in
            return Channels(channels: publicChannels.channels + privateChannels.channels)
        }
    }

    func users(forChannel channel: Channel) -> Observable<Users> {
        return channelsDatabase.observeOwnerIdsFor(channel)
            .flatMap(getUsersFromIDs)
    }

    // MARK: - Read (Private)

    private func publicChannels() -> Observable<Channels> {
        return channelsDatabase.observePublicChannelIds()
            .flatMap(channelsFromNames)
    }

    private func privateChannels(forUser user: User) -> Observable<Channels> {
        return channelsDatabase.observePrivateChannelIdsFor(user)
            .flatMap(channelsFromNames)
    }

    private func channelsFromNames(channelNames: [String]) -> Observable<Channels> {
        return channelNames.toObservable().flatMap(channelFromName).toArray().map(Channels.init)
    }

    private func channelFromName(channelName: String) -> Observable<Channel> {
        return channelsDatabase.readChannelFor(channelName).catchError({_ in return Observable.empty()})
    }

    private func getUsersFromIDs(ids: [String]) -> Observable<Users> {
        return ids.toObservable().flatMap(readUserFrom).toArray().map(Users.init)
    }

    private func readUserFrom(identifier: String) -> Observable<User> {
        return userDatabase.readUserFrom(identifier).catchError({_ in Observable.empty()})
    }

    // MARK: - Write

    func createPublicChannel(withName name: String) -> Observable<DatabaseResult<Channel>> {
        let channel = Channel(name: name, access: .Public)
        return channelsDatabase.writeChannel(channel)
            .flatMap(writeChannelToChannelIndexDB)
            .catchError({Observable.just(DatabaseResult.Error($0))})
    }

    func createPrivateChannel(withName name: String, owner: User) -> Observable<DatabaseResult<Channel>> {
        let channel = Channel(name: name, access: .Private)
        return channelsDatabase.addOwnerToPrivateChannel(owner, channel: channel)
            .flatMap({self.channelsDatabase.writeChannel($0)})
            .flatMap(addUserAsChannelOwner(owner))
            .map({.Success($0)})
            .catchError({Observable.just(DatabaseResult.Error($0))})
    }

    func addOwner(owner: User, toPrivateChannel channel: Channel) -> Observable<DatabaseResult<User>> {
        return channelsDatabase.addOwnerToPrivateChannel(owner, channel: channel)
            .flatMap(addUserAsChannelOwner(owner))
            .map({_ in .Success(owner)})
            .catchError({Observable.just(DatabaseResult.Error($0))})
    }

    func removeOwner(owner: User, fromPrivateChannel channel: Channel) -> Observable<DatabaseResult<User>> {
        return channelsDatabase.removeOwnerFromPrivateChannel(owner, channel: channel)
            .flatMap({channel in
                self.channelsDatabase.removeChannelFromUserPrivateChannelIndex(owner, channel: channel)
            }).map({_ in .Success(owner)})
            .catchError({Observable.just(DatabaseResult.Error($0))})
    }


    // MARK: - Write (Private)

    private func writeChannelToChannelIndexDB(channel: Channel) -> Observable<DatabaseResult<Channel>> {
        return channelsDatabase.writeChannelToPublicChannelIndex(channel)
            .map({.Success($0)})
    }

    private func writeChannel(channel: Channel) -> Observable<DatabaseResult<Channel>> {
        return channelsDatabase.writeChannel(channel)
            .map({.Success($0)})
    }

    private func addUserAsChannelOwner(owner: User) -> (Channel -> Observable<Channel>) {
        return { channel in
            return self.channelsDatabase.addChannelToUserPrivateChannelIndex(owner, channel: channel)
        }
    }
}
