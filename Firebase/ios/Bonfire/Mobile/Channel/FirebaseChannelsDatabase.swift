import Foundation
import RxSwift
import Firebase

class FirebaseChannelsDatabase: ChannelsDatabase {

    private let publicChannelsDB: FIRDatabaseReference
    private let privateChannelsDB: FIRDatabaseReference
    private let channelsDB: FIRDatabaseReference
    private let ownersDB: FIRDatabaseReference

    init(publicChannelsDB: FIRDatabaseReference,
         privateChannelsDB: FIRDatabaseReference,
         channelsDB: FIRDatabaseReference,
         ownersDB: FIRDatabaseReference) {
        self.publicChannelsDB = publicChannelsDB
        self.privateChannelsDB = privateChannelsDB
        self.channelsDB = channelsDB
        self.ownersDB = ownersDB
    }

    func observePublicChannelIds() -> Observable<[String]> {
        return publicChannelsDB.rx_readValue().map(getKeys)
    }

    func observePrivateChannelIdsFor(user: User) -> Observable<[String]> {
        return privateChannelsDB.child(user.identifier).rx_readValue().map(getKeys)
    }

    func readChannelFor(channelName: String) -> Observable<Channel> {
        return channelsDB.child(channelName).rx_readOnce().map(Channel.init)
    }

    func writeChannel(newChannel: Channel) -> Observable<Channel> {
        return channelsDB.child(newChannel.name).rx_write(newChannel.asFirebaseValue()).map {newChannel}
    }

    func writeChannelToPublicChannelIndex(newChannel: Channel) -> Observable<Channel> {
        return publicChannelsDB.child(newChannel.name).rx_write(true).map {newChannel}
    }

    func addOwnerToPrivateChannel(user: User, channel: Channel) -> Observable<Channel> {
        return ownersDB.child(channel.name).child(user.identifier).rx_write(true).map {channel}
    }

    func removeOwnerFromPrivateChannel(user: User, channel: Channel) -> Observable<Channel> {
        return ownersDB.child(channel.name).child(user.identifier).rx_delete().map {channel}
    }

    func addChannelToUserPrivateChannelIndex(user: User, channel: Channel) -> Observable<Channel> {
        return privateChannelsDB.child(user.identifier).child(channel.name).rx_write(true).map {channel}
    }

    func removeChannelFromUserPrivateChannelIndex(user: User, channel: Channel) -> Observable<Channel> {
        return privateChannelsDB.child(user.identifier).child(channel.name).rx_delete().map {channel}
    }

    func observeOwnerIdsFor(channel: Channel) -> Observable<[String]> {
        return ownersDB.child(channel.name).rx_readValue().map(getKeys)
    }

    private func getKeys(snapshot: FIRDataSnapshot) -> [String] {
        return snapshot.children.allObjects.map {$0.key!}
    }
}
