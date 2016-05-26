import Foundation
import Firebase
import FirebaseAnalytics
import FirebaseInstanceID
import FirebaseDatabase
import RxSwift

enum ChannelCreation {
    case Idle
    case Busy
    case Successful
    case Error
}

enum DatabaseWriteResult<T> {
    case Success(T)
    case Error(ErrorType)
}

func convertToFirebaseOwners(owners: [User]) -> AnyObject {
    var dic = [String: Bool]()
    owners.forEach({ val in
        dic[val.id] = true
    })
    return dic
}

class FirebaseChannelsService: ChannelsService {

    let firebase = FIRDatabase.database().reference()

    private func channelsInfo(withKey key: String) -> FIRDatabaseReference {
        return firebase.child("channels/\(key)")
    }

    private func owners() -> FIRDatabaseReference {
        return firebase.child("owners")
    }

    private func ownersList(withKey key: String) -> FIRDatabaseReference {
        return owners().child(key)
    }

    private func channelsIndex() -> FIRDatabaseReference {
        return firebase.child("public-channels-index")
    }

    private func privateChannelsIndex(forUser user: User) -> FIRDatabaseReference {
        return firebase.child("private-channels-index").child(user.id)
    }

    // MARK: - Read
    func channels(forUser user: User) -> Observable<[Channel]> {
        return Observable.combineLatest(publicChannels(), privateChannels(forUser: user)) { publicChannels, privateChannels in
            return publicChannels + privateChannels
        }
    }

    func users(forChannel channel: Channel) -> Observable<[User]> {
        return ownersList(withKey: channel.name).rx_readValue()
            .map({ snapshot in
                let firebaseUserIndexes = snapshot.children.allObjects
                return firebaseUserIndexes.map({self.user($0.key)})
            })
            .flatMap(mergeToArray)
    }

    private func user(identifier: String) -> Observable<User> {
        return firebase.child("users").child(identifier)
            .rx_readOnce()
            .map({snapshot in
                if let userFirebaseValue = snapshot.value,
                    let user = try? User(firebaseValue: userFirebaseValue) {
                    return user
                }
                return nil
            })
            .filter({$0 != nil})
            .map({$0!})
    }

    private func publicChannels() -> Observable<[Channel]> {
        return Observable.create({ observer in

            let handle = self.channelsIndex().observeEventType(.Value, withBlock: { snapshot in
                let firebaseChannels: [Observable<Channel>] = snapshot.children.allObjects.map{$0.key!}.map(self.channel)
                observer.onNext(firebaseChannels)
            })

            return AnonymousDisposable() {
                self.firebase.removeObserverWithHandle(handle)
            }

        }).flatMap(mergeToArray)
    }

    private func privateChannels(forUser user: User) -> Observable<[Channel]> {
        return Observable.create({ observer in

            let handle = self.privateChannelsIndex(forUser: user).observeEventType(.Value, withBlock: { snapshot in
                let firebaseChannels: [Observable<Channel>] = snapshot.children.allObjects.map{$0.key!}.map(self.channel)
                observer.onNext(firebaseChannels)
            })

            return AnonymousDisposable() {
                self.firebase.removeObserverWithHandle(handle)
            }

        }).flatMap(mergeToArray)
    }

    private func channel(withKey key: String) -> Observable<Channel> {
        return Observable.create({ observer in
            self.channelsInfo(withKey: key).observeSingleEventOfType(.Value,
                withBlock: { snapshot in
                    if let channelFirebaseValue = snapshot.value,
                        let channel = try? Channel(firebaseValue: channelFirebaseValue) {
                        observer.onNext(channel)
                    } else {
                        print("Couldn't find or parse \(key)")
                        observer.onCompleted()
                    }
                    observer.on(.Completed)
                }, withCancelBlock: { error in
                    print("problem! \(error)")
                    observer.onCompleted()
                }
            )

            return AnonymousDisposable() {}
        })
    }

    // MARK: - Write
    func createPublicChannel(withName name: String) -> Observable<DatabaseWriteResult<Channel>> {
        let name = name.stringByTrimmingCharactersInSet(.whitespaceCharacterSet())

        let channel = Channel(name: name, access: .Public)
        return self.channelsInfo(withKey: name)
            .rx_write(channel.asFirebaseValue())
            .flatMap({self.channelsIndex().child(channel.name).rx_write(true)})
            .map({DatabaseWriteResult.Success(channel)})
            .catchError({Observable.just(DatabaseWriteResult.Error($0))})
    }

    func createPrivateChannel(withName name: String, owners: [User]) -> Observable<DatabaseWriteResult<Channel>> {
        let name = name.stringByTrimmingCharactersInSet(.whitespaceCharacterSet())
        let channel = Channel(name: name, access: .Private)
        let firebaseOwners = convertToFirebaseOwners(owners)

        return self.ownersList(withKey: name)
            .rx_write(firebaseOwners)
            .flatMap({
                owners.toObservable().flatMap({ user in
                    self.privateChannelsIndex(forUser: user).child(channel.name).rx_write(true)
                })
            })
            .flatMap({
                self.channelsInfo(withKey: name).rx_write(channel.asFirebaseValue())
            })
            .map({DatabaseWriteResult.Success(channel)})
            .catchError({Observable.just(DatabaseWriteResult.Error($0))})
    }

    func addOwners(owners: [User], channel: Channel) -> Observable<DatabaseWriteResult<[User]>> {
        return owners.toObservable()
            .flatMap ({ user in
                self.ownersList(withKey: channel.name)
                    .child(user.id)
                    .rx_write(true)
                    .flatMap({
                        self.privateChannelsIndex(forUser: user)
                            .child(channel.name)
                            .rx_write(true)
                    })
            })
            .map({DatabaseWriteResult.Success(owners)})
            .catchError({Observable.just(DatabaseWriteResult.Error($0))})
            .takeLast(1)
    }

    func removeOwners(owners: [User], channel: Channel) -> Observable<DatabaseWriteResult<[User]>> {
        return owners.toObservable()
            .flatMap ({ user in
                self.ownersList(withKey: channel.name)
                    .child(user.id)
                    .rx_delete()
                    .flatMap({
                        self.privateChannelsIndex(forUser: user)
                            .child(channel.name)
                            .rx_delete()
                    })
            })
            .map({DatabaseWriteResult.Success(owners)})
            .catchError({Observable.just(DatabaseWriteResult.Error($0))})
            .takeLast(1)
    }
    
}
